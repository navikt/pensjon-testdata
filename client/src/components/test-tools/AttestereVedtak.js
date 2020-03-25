import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import {SnackbarContext} from "../support/Snackbar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import {makeStyles} from '@material-ui/core/styles';
import FingerprintIcon from "@material-ui/icons/Fingerprint";
import {callURL} from "../../util/rest";

const useStyles = makeStyles({
    card: {
        minWidth: 275,
        maxWidth: 300
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
        callURL(
            '/api/attester',
            'POST',
            {vedtakId: vedtakId},
            () => {
                snackbarApi.openSnackbar('Vedtak attestert', 'success');
            },
            () => {
                snackbarApi.openSnackbar('Attestering av vedtak feilet!', 'error');
            }
        ).finally(() => {
                setIsProcessing(false);
            }
        );
    };

    return (
        <Card className={classes.card} variant="outlined">
            <CardHeader title="Attestere vedtak"/>
            <CardContent>
                <p>Gjennomfører attestering av vedtak i PEN, merk at løsningen <b>ikke</b> benytter tjenestelaget til PEN, og er derfor en forenkling av løsningen som faktisk vil benyttes ved ordinær saksbehandling.</p>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Vedtaks-ID"
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