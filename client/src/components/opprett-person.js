import React, {useState} from 'react';
import CircularProgress from "@material-ui/core/CircularProgress";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";

import {SnackbarContext} from "./snackbar";


const OpprettPerson = () => {
    const [isProcessing, setIsProcessing] = useState(false);
    const [fnr, setFnr] = useState('');

    const snackbarApi = React.useContext(SnackbarContext);

    const lagre = (event) => {
        setIsProcessing(true);
        fetch('/api/person/' + fnr, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify('')
        }).then(function (response) {
            if (response.status === 200) {
                snackbarApi.openSnackbar('Person opprettet!', 'success');
            } else {
                snackbarApi.openSnackbar('Lagring av person feilet!', 'error')
            }
            setIsProcessing(false);
        })
    };


    return (
        <div style={{textAlign: 'left', width: '40%', maxWidth: '20rem', margin: '0 auto'}}>
            <h4>Opprett person</h4>
            <TextField label="Fnr"
                       name="fnr"
                       key="fnr"
                       variant="outlined"
                       onChange={e => setFnr(e.target.value.trim())}/><br/><br/>

            {isProcessing ? <CircularProgress/> :
                <Button variant="contained" onClick={() => lagre()}>Lagre</Button>}
        </div>
    );
}


export default OpprettPerson