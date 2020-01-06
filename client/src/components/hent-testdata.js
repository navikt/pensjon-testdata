import React, {Component} from 'react';
import Button from "@material-ui/core/Button";
import CircularProgress from "@material-ui/core/CircularProgress";
import TextareaAutosize from '@material-ui/core/TextareaAutosize';
import IconButton from '@material-ui/core/IconButton';
import AddBoxIcon from '@material-ui/icons/AddBox';
import Chip from "@material-ui/core/Chip";
import Box from "@material-ui/core/Box";
import TextField from '@material-ui/core/TextField';
import moment from "moment";
import CloudDownloadIcon from '@material-ui/icons/CloudDownload';


class HentTestdata extends Component {
    state = {
        isProcessing: false,
        fom: moment(new Date()).subtract('3', 'hours').format("YYYY-MM-DD HH:mm:ss"),
        tom: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
        data: [],
        caseworkers: []
    };

    downloadResult = () => {
        const element = document.createElement("a");
        const file = new Blob([this.transformData()], {type: 'text/plain'});
        element.href = URL.createObjectURL(file);
        element.download = "testada.sql";
        document.body.appendChild(element); // Required for this to work in FireFox
        element.click();
    }

    updateCaseworkers = (data) => {
        this.setState({caseworkers: data});
    }

    hent = (event) => {
        this.setState({isProcessing: true})

        let request = JSON.stringify({
            fom: this.state.fom,
            tom: this.state.tom,
            identer: this.state.caseworkers.map(caseworker => caseworker.name)
        });

        console.log(request);

        fetch('/api/moog/testdata', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: request
        }).then(response => {
            return response.json()
        }).then(json => {
            console.log("Fetched testdata from server, recieved " + json.length + " elements");
            this.setState({
                data: json
            });
        }).finally((data) => {
            this.setState({isProcessing: false})
        });
    };

    onChange = (event) => {
        let stateObject = function () {
            let returnObj = {};
            returnObj[this.target.name] = this.target.value;
            return returnObj;
        }.bind(event)();

        this.setState(stateObject);
    };

    transformData() {
        var result = '';
        this.state.data.forEach(function (sql) {
            result = result + '\r\n' + sql.replace(';', '');
        });
        return result.substring(2);
    }

    render() {
        return (
            <div >
                <div style={{textAlign: 'left', width: '40%', maxWidth: '20rem', margin: '0 auto'}}>
                    <form style={{margin: '10px'}}>
                        <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                                   label="Fom"
                                   name="fom"
                                   key="fom"
                                   variant="outlined" //2019-11-29 08:40:00
                                   defaultValue={this.state.fom}
                                   onChange={this.onChange}/>
                        <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                                   label="Tom"
                                   name="tom"
                                   key="tom"
                                   variant="outlined"
                                   defaultValue={this.state.tom}
                                   onChange={this.onChange}/>
                    </form>
                    <CaseworkerChips data={this.updateCaseworkers}/>

                    {this.state.isProcessing ? <CircularProgress /> :
                        <Button variant="contained"  onClick={this.hent}>Hent</Button>}

                    <p></p>
                </div>

                <div style={{textAlign: 'center'}}>
                {this.state.data.length > 0 ?
                    <div> Last ned resultat
                        <IconButton onClick={this.downloadResult}>
                            <CloudDownloadIcon/>
                        </IconButton>
                    </div> : <p></p>}

                {this.state.data.length > 0 ?
                    <TextareaAutosize value={this.transformData()}
                                      style={{
                                          widht: '800px',
                                          overflow: 'auto',
                                          whiteSpace: 'nowrap',
                                          minWidth: '80%',
                                          minHeight: 450
                                      }}></TextareaAutosize> : <p></p>}
                </div>
            </div>
        );
    }
}

function CaseworkerChips(props) {
    const [caseWorkersData, setcaseWorkersData] = React.useState([]);
    const [caseworker, setCaseworker] = React.useState('');

    const handleDelete = chipToDelete => () => {
        setcaseWorkersData(chips => chips.filter(chip => chip.key !== chipToDelete.key));
    };

    const addChip = () => {
        if (caseworker.length > 0) {
            const newData = [...caseWorkersData];
            newData.push({key: caseWorkersData.length, name: caseworker});
            setcaseWorkersData(newData);
            setCaseworker('');
            props.data(newData);
        }
    };

    return (
        <div>
            <Box component="div" display="inline">
                <form style={{margin: '10px'}}>
                    <TextField style={{textAlign: 'left'}} label="Saksbehandler" name="caseworker"
                               key="caseworker"
                               value={caseworker}
                               onChange={e => setCaseworker(e.target.value)}
                               variant="outlined"/>
                    <IconButton onClick={addChip}>
                        <AddBoxIcon/>
                    </IconButton>
                </form>
            </Box>
            <div style={{marginBottom: '10px', marginTop: '10px'}}>
                {caseWorkersData.map((data) => {
                    return (
                        <Chip
                            key={data.key}
                            label={data.name}
                            onDelete={handleDelete(data)}
                        />
                    );
                })}
            </div>
        </div>
    );
}


export default HentTestdata