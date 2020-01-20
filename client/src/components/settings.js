import React, {Component} from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Switch from '@material-ui/core/Switch';
import FormControlLabel from "@material-ui/core/FormControlLabel";


class Settings extends Component {
    state = {
        isProcessing: false,
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
        this.setState({developer: JSON.parse(localStorage.getItem('pensjon-testdata-developer'))})
    }


    lagre = (event) => {
        console.log("Lagre");
        this.setState({isProcessing: true})
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