import React, {Component} from 'react';
import CircularProgress from "@material-ui/core/CircularProgress";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";


class OpprettPerson extends Component {
    state = {
        isProcessing: false,
        fnr: ''
    };

    lagre = (event) => {
        console.log("Lagre");
        this.setState({isProcessing: true})
        fetch('/moog/person/' + this.state.fnr, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify('')
        }).finally((data) => {
            this.setState({isProcessing: false})
        });
    };

    fnrChange = (event) => {
        this.setState({fnr: event.target.value});
    };

    render() {
        return (
            <div style={{textAlign: 'left', width: '40%', maxWidth: '20rem', margin: '0 auto'}}>
                <h4>Opprett person</h4>
                <TextField label="Fnr"
                           name="fnr"
                           key="fnr"
                           variant="outlined"
                           onChange={this.fnrChange}/><br/><br/>

                {this.state.isProcessing ? <CircularProgress/> :
                    <Button variant="contained" onClick={this.lagre}>Lagre</Button>}

            </div>
        );
    }
}

export default OpprettPerson