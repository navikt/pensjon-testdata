import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import {SnackbarContext} from "./Snackbar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import {makeStyles} from '@material-ui/core/styles';
import FingerprintIcon from "@material-ui/icons/Fingerprint";

const useStyles = makeStyles({
    card: {
        minWidth: 275,
        maxWidth: 300
    },
    bullet: {
        display: 'inline-block',
        margin: '0 2px',
        transform: 'scale(0.8)',
    },
    title: {
        fontSize: 14,
    },
    pos: {
        marginBottom: 12,
    },
});

const AttestereVedtak = () => {
    const classes = useStyles();

    const [isProcessing, setIsProcessing] = useState(false);
    const [vedtakId, setVedtakId] = useState('');

    const snackbarApi = React.useContext(SnackbarContext);

    const attestereVedtak = () => {
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

    return (
        <Card className={classes.card} variant="outlined">
            <CardHeader title="Attestere vedtak"/>
            <CardContent>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="VedtakId"
                           name="vedtakid"
                           key="vedtakid"
                           variant="outlined"
                           onChange={e => setVedtakId(e.target.value)}/>
            </CardContent>
            <CardActions disableSpacing>
                <Button onClick={() => attestereVedtak()}
                        variant="contained"
                        disabled={isProcessing ? true : false}
                        startIcon={<FingerprintIcon/>}>
                    Attester</Button>
            </CardActions>
        </Card>
    );
}


export default AttestereVedtak