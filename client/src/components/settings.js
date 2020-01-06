import React, {Component} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import Switch from '@material-ui/core/Switch';
import FormControlLabel from "@material-ui/core/FormControlLabel";


class Settings extends Component {
    state = {
        isProcessing: false,
        server: '',
        database: '',
        brukernavn: '',
        passord: '',
        developer: false
    };

    useStyles = makeStyles(theme => ({
        root: {
            'MuiInputBase-input': {
                margin: theme.spacing(1),
                marginBottom: '50px',
                width: 200
            },

        },
    }));

    componentDidMount() {
        this.setState({server: localStorage.getItem('pensjon-testdata-db-server')});
        this.setState({database: localStorage.getItem('pensjon-testdata-db-database')});
        this.setState({brukernavn: localStorage.getItem('pensjon-testdata-db-brukernavn')});
        this.setState({passord: localStorage.getItem('pensjon-testdata-db-passord')});
        this.setState({developer: JSON.parse(localStorage.getItem('pensjon-testdata-developer'))})
    }


    lagre = (event) => {
        console.log("Lagre");
        this.setState({isProcessing: true})
        localStorage.setItem('pensjon-testdata-db-server', this.state.server);
        localStorage.setItem('pensjon-testdata-db-database', this.state.database);
        localStorage.setItem('pensjon-testdata-db-brukernavn', this.state.brukernavn);
        localStorage.setItem('pensjon-testdata-db-passord', this.state.passord);
        localStorage.setItem('pensjon-testdata-developer', this.state.developer);
        this.setState({isProcessing: false})
        this.props.action();
    };

    onChange = (event) => {
        let stateObject = function () {
            let returnObj = {};
            returnObj[this.target.name] = this.target.value;
            return returnObj;
        }.bind(event)();
        this.setState(stateObject);
    };

    onCheckboxChange = (event) => {
        this.setState({developer: event.target.checked})
        this.props.darkMode();
    }

    render() {
        return (
            <div className={this.useStyles.root}  style={{textAlign: 'left', width: '40%', maxWidth: '20rem', margin: '0 auto'}}>

                    <TextField label="Databaseserver" name="server" key="server"
                               value={this.state.server}
                               variant="outlined"
                               onChange={this.onChange}/><br/><br/>
                    <TextField label="Database"
                               name="database"
                               key="database"
                               value={this.state.database}
                               variant="outlined"
                               onChange={this.onChange}/><br/><br/>
                    <TextField label="Brukernavn"
                               name="brukernavn"
                               key="brukernavn"
                               value={this.state.brukernavn}
                               variant="outlined"
                               onChange={this.onChange}/><br/><br/>
                    <TextField  label="Passord" name="passord" key="passord"
                               type="password" value={this.state.passord}
                               variant="outlined"
                               onChange={this.onChange}/><br/><br/>

                <FormControlLabel
                    control={
                        <Switch
                            name={"developer"}
                            checked={this.state.developer}
                            onChange={this.onCheckboxChange}
                            color="primary"
                            value={"developer"}
                        />
                    }
                    label="Dark mode"
                />
                <p>
                    <Button variant="contained" onClick={this.lagre}>Lagre</Button>
                </p>

            </div>
        );
    }
}

export default Settings