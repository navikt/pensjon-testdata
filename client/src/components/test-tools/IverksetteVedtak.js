import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import {SnackbarContext} from "../support/Snackbar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import {makeStyles} from '@material-ui/core/styles';
import FlashOnIcon from "@material-ui/icons/FlashOn";
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

const IverksetteVedtak = () => {
    const classes = useStyles();

    const [isProcessing, setIsProcessing] = useState(false);
    const [vedtakId, setVedtakId] = useState('');

    const [vedtakIdValidationText, setVedtakIdValidationText] = useState('');

    const snackbarApi = React.useContext(SnackbarContext);

    const resetValidation = () => {
        setVedtakIdValidationText("");
    }

    const iverksetteVedtak = () => {
        resetValidation();
        if (!/^\d+$/.test(vedtakId)) {
            setVedtakIdValidationText("Må inneholde tall")
        } else {
            setIsProcessing(true);
            callURL(
                '/api/iverksett',
                'POST',
                {
                    vedtakId: vedtakId
                },
                () => {
                    snackbarApi.openSnackbar('Vedtak iverksatt', 'success');
                },
                () => {
                    snackbarApi.openSnackbar('Iverksetting feilet!', 'error');
                }
            ).finally(() => {
                    setIsProcessing(false);
                }
            );
        }
    };

    return (
        <Card className={classes.card}>
            <CardHeader title="Iverksette vedtak"/>
            <CardContent>
                <p>Gjennomfører iverksetting av vedtak i PEN, merk at løsningen <b>ikke</b> benytter tjenestelaget til PEN, og ikke overfører ytelsen til Oppdrag. Kan ikke sees på som en fullverdig test av iverksetting.</p>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Vedtaks-ID"
                           name="vedtakid"
                           key="vedtakid"
                           variant="outlined"
                           onChange={e => setVedtakId(e.target.value)}
                           helperText={vedtakIdValidationText}
                           error={vedtakIdValidationText.length !== 0}
                />

            </CardContent>
            <CardActions>
                <Button onClick={() => iverksetteVedtak()}
                        variant="contained"
                        disabled={isProcessing ? true : false}
                        startIcon={<FlashOnIcon/>}>
                    Iverksett
                </Button>

            </CardActions>
        </Card>
    );
}

export default IverksetteVedtak