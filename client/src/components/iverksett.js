import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import FlashOnIcon from '@material-ui/icons/FlashOn';
import FingerprintIcon from '@material-ui/icons/Fingerprint';
import AccountTreeIcon from '@material-ui/icons/AccountTree';
import CircularProgress from "@material-ui/core/CircularProgress";
import Button from '@material-ui/core/Button';
import {SnackbarContext} from "./snackbar";

const Iverksett = () => {
    const [isProcessing, setIsProcessing] = useState(false);
    const [vedtakId, setVedtakId] = useState('');
    const [sakId, setSakId] = useState('');
    const [nyEnhetId, setNyEnhetId] = useState('');

    const snackbarApi = React.useContext(SnackbarContext);

    const iverksett = () => {
        setIsProcessing(true);
        let body = {
            vedtakId: vedtakId
        }

        fetch('/api/iverksett', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
        }).then(response => {
            if (response.status === 200) {
                snackbarApi.openSnackbar('Vedtak iverksatt', 'success');
            } else {
                snackbarApi.openSnackbar('Iverksetting feilet!', 'error');
            }
        }).finally((data) => {
            setIsProcessing(false);
        });
    };

    const attester = () => {
        setIsProcessing(true);
        let body = {
            vedtakId: vedtakId
        }
        fetch('/api/attester', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
        }).then(response => {
            if (response.status === 200) {
                snackbarApi.openSnackbar('Vedtak attestert', 'success');
            } else {
                snackbarApi.openSnackbar('Attestering av vedtak feilet!', 'error');
            }
        }).finally((data) => {
            setIsProcessing(false);
        });
    };

    const flyttEnhet = (event) => {
        setIsProcessing(true);

        fetch('/api/flytte-sak', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                sakId: sakId,
                nyEnhet: nyEnhetId
            })
        }).then(response => {
            if (response.status === 200) {
                snackbarApi.openSnackbar('Enhet flyttet', 'success');
            } else {
                snackbarApi.openSnackbar('Flytting av enhet feilet!', 'error');
            }
            setIsProcessing(false);
        });
    };


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
                               onChange={e => setSakId(e.target.value)}/>
                    <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                               label="Ny enhet"
                               name="nyEnhet"
                               key="nyEnhet"
                               variant="outlined"
                               onChange={e => setNyEnhetId(e.target.value)}/>
                    <Button onClick={() => flyttEnhet()}
                            variant="contained"
                            startIcon={<AccountTreeIcon/>}
                            disabled={isProcessing ? true : false}>
                        Flytt til enhet</Button>
                </form>

                <form style={{margin: '10px'}}>
                    <h4>Vedtaksbehandling</h4>
                    <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                               label="VedtakId"
                               name="vedtakid"
                               key="vedtakid"
                               variant="outlined"
                               onChange={e => setVedtakId(e.target.value)}/>
                    <Button onClick={() => attester()}
                            variant="contained"
                            disabled={isProcessing ? true : false}
                            startIcon={<FingerprintIcon/>}>
                        Attester</Button>
                    <Button onClick={() => iverksett()}
                            style={{marginLeft: '10px'}} variant="contained"
                            disabled={isProcessing ? true : false}
                            startIcon={<FlashOnIcon/>}>
                        Iverksett</Button>
                </form>

                {!isProcessing ? "" : <CircularProgress/>}
            </div>
        </div>
    );


}


export default Iverksett