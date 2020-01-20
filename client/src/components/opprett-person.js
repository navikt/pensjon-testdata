import React, {useState} from 'react';
import CircularProgress from "@material-ui/core/CircularProgress";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";
import Snackbar from "@material-ui/core/Snackbar";
import Alert from '@material-ui/lab/Alert';


const OpprettPerson = () => {
    const [isProcessing, setIsProcessing] = useState(false);
    const [fnr, setFnr] = useState('');

    const [open, setOpen] = React.useState(false);
    const [messageType, setMessageType] = React.useState('error');
    const [message, setMessage] = React.useState('');

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setOpen(false);
    };

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
            console.log(response);
            if(response.status === 200) {
                setMessageType('success');
                setMessage('Person opprettet!');
            } else {
                setMessageType('error');
                setMessage('Lagring av person feilet!');
            }
            setOpen(true);
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
                       onChange={e => setFnr(e.target.value)}/><br/><br/>

            {isProcessing ? <CircularProgress/> :
                <Button variant="contained" onClick={() => lagre()}>Lagre</Button>}

            <Snackbar
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'left',
                }}
                open={open}
                autoHideDuration={6000}
                onClose={handleClose}>
                <Alert onClose={handleClose} variant="filled" severity={messageType}>
                    {message}
                </Alert>
            </Snackbar>
        </div>
    );
}

export default OpprettPerson