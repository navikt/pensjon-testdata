import React, {Component} from 'react';
import TextField from '@material-ui/core/TextField';
import FlashOnIcon from '@material-ui/icons/FlashOn';
import FingerprintIcon from '@material-ui/icons/Fingerprint';
import AccountTreeIcon from '@material-ui/icons/AccountTree';
import CircularProgress from "@material-ui/core/CircularProgress";
import Button from '@material-ui/core/Button';

class Iverksett extends Component {
    state = {
        isProcessing: false
    };


    hent = (event) => {
        this.setState({isProcessing: true})
        let body = {
            vedtakId: this.state.vedtakid,
            server: localStorage.getItem('pensjon-testdata-db-server'),
            database: localStorage.getItem('pensjon-testdata-db-database'),
            username: localStorage.getItem('pensjon-testdata-db-brukernavn'),
            password: localStorage.getItem('pensjon-testdata-db-passord')
        }

        console.log(body);

        fetch('/api/iverksett', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
        }).finally((data) => {
            this.setState({isProcessing: false})
        });
    };

    attester = (event) => {
        this.setState({isProcessing: true})
        let body = {
            vedtakId: this.state.vedtakid,
            server: localStorage.getItem('pensjon-testdata-db-server'),
            database: localStorage.getItem('pensjon-testdata-db-database'),
            username: localStorage.getItem('pensjon-testdata-db-brukernavn'),
            password: localStorage.getItem('pensjon-testdata-db-passord')
        }
        fetch('/api/attester', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
        }).finally((data) => {
            this.setState({isProcessing: false})
        });
    };

    flytt = (event) => {
        this.setState({isProcessing: true})
        let body = {
            sakId: this.state.sakid,
            nyEnhet: this.state.nyEnhet,
            server: localStorage.getItem('pensjon-testdata-db-server'),
            database: localStorage.getItem('pensjon-testdata-db-database'),
            username: localStorage.getItem('pensjon-testdata-db-brukernavn'),
            password: localStorage.getItem('pensjon-testdata-db-passord')
        }

        console.log(body);

        fetch('/api/flytte-sak', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
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


    render() {
        return (
            <div>
                <div style={{textAlign: 'left', width: '40%', maxWidth: '20rem', margin: '10 auto'}}>

                    <form style={{margin: '10px'}}>
                        <h4>Flytt eierenhet for sak</h4>
                        <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                                   label="SakId"
                                   name="sakid"
                                   key="sakid"
                                   variant="outlined"
                                   onChange={this.onChange}/>
                        <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                                   label="Ny enhet"
                                   name="nyEnhet"
                                   key="nyEnhet"
                                   variant="outlined"
                                   onChange={this.onChange}/>
                        <Button onClick={this.flytt}
                                variant="contained"
                                startIcon={<AccountTreeIcon/>}
                                disabled={this.state.isProcessing ? true : false}>
                            Flytt til enhet</Button>
                    </form>

                    <form style={{margin: '10px'}}>
                        <h4>Vedtaksbehandling</h4>
                        <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                                   label="VedtakId"
                                   name="vedtakid"
                                   key="vedtakid"
                                   variant="outlined"
                                   onChange={this.onChange}/>
                        <Button onClick={this.attester}
                                variant="contained"
                                disabled={this.state.isProcessing ? true : false}
                                startIcon={<FingerprintIcon/>} >
                            Attester</Button>
                        <Button onClick={this.hent}
                                style={{marginLeft: '10px'}} variant="contained"
                                disabled={this.state.isProcessing ? true : false}
                                startIcon={<FlashOnIcon/>} >
                            Iverksett</Button>
                    </form>

                    {!this.state.isProcessing ? "" : <CircularProgress/>}
                </div>
            </div>
        );
    }

}


export default Iverksett