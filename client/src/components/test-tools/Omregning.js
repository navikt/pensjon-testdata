import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import {SnackbarContext} from "../support/Snackbar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import {makeStyles} from '@material-ui/core/styles';
import FastForwardIcon from '@material-ui/icons/FastForward';
import DateFnsUtils from '@date-io/date-fns';
import {KeyboardDatePicker, MuiPickersUtilsProvider,} from '@material-ui/pickers';


const useStyles = makeStyles({
    card: {
        minWidth: 350,
        maxWidth: 350
    },
    title: {
        fontSize: 14,
    },
    pos: {
        marginBottom: 12,
    },
});


const Omregning = () => {
    const classes = useStyles();

    const defaultVirkfom = () => {
        let now = new Date();
        let current;
        if (now.getMonth() === 11) {
            current = new Date(now.getFullYear() + 1, 0, 1);
        } else {
            current = new Date(now.getFullYear(), now.getMonth() + 1, 1);
        }
        return current;
    }

    const [isProcessing, setIsProcessing] = useState(false);
    const [sakId, setSakId] = useState('');
    const [sakIdValidationText, setSakIdValidationText] = useState('');
    const [virkFom, setVirkFom] = useState(defaultVirkfom());

    const snackbarApi = React.useContext(SnackbarContext);

    const resetValidation = () => {
        setSakIdValidationText('');
    }

    const omregning = async () => {
        resetValidation();

        if (!/^\d*$/.test(sakId.trim())) {
            setSakIdValidationText("Må inneholde tall")
        }

        if (isNotValid(sakIdValidationText)) {
            snackbarApi.openSnackbar('Feil i validering, omregning ble ikke gjennomført', 'error');
        } else {
            execute();
        }
    }

    function leftPadMonth (str, len, ch) {
        if (!ch) {
            ch = '.';
        }

        str = String(str);
        if (ch.length !== 1) {
            throw 'Invalid Input'
        }

        len = len - str.length;
        let strCh = '';
        for (let i = 0; i < len; ++i) {
            strCh += ch;
        }

        return (strCh + str);
    }

    const execute = async () => {
        setIsProcessing(true);
        const response = await fetch('/api/omregning', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                sakId: sakId.trim(),
                virkFom:    '01-' + leftPadMonth(''+virkFom.getMonth(),2,'0') + '-' + virkFom.getFullYear()
            })
        });
        let body = await response.text();
        console.log(response.status);
        console.log(body)

        if (response.status === 200) {
            snackbarApi.openSnackbar(body, 'success');
        } else {
            snackbarApi.openSnackbar('Omregning ble ikke gjennomført' + body, 'error');
        }

        setIsProcessing(false);
    }

    const isNotValid = (value) => {
        return value.length !== 0
    }

    return (
        <Card className={classes.card}>
            <CardHeader title="Automatisk omregning"/>
            <CardContent>
                <p>Vil benytte tjenester i Pesys gjennomføre automatisk omregning av en ytelse, tilsvarende omregning med batchen <i>BPEN093 Omregning</i></p>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px', width: 300}}
                           label="Saksnummer"
                           name="sakid"
                           key="sakId"
                           variant="outlined"
                           helperText={sakIdValidationText}
                           onChange={e => setSakId(e.target.value)}
                           error={isNotValid(sakIdValidationText)}
                /><br/>
                <MuiPickersUtilsProvider utils={DateFnsUtils}>
                    <KeyboardDatePicker
                        margin="normal"
                        id="date-picker-dialog"
                        label="Velg virkningstidspunkt"
                        format="MM/dd/yyyy"
                        value={virkFom}
                        onChange={setVirkFom}
                        KeyboardButtonProps={{
                            'aria-label': 'Virkningstidspunkt',
                        }}
                    />
                </MuiPickersUtilsProvider>
            </CardContent>
            <CardActions>
                <Button onClick={() => omregning()}
                        variant="contained"
                        disabled={isProcessing ? true : false}
                        startIcon={<FastForwardIcon/>}>
                    Gjennomfør omregning
                </Button>
            </CardActions>
        </Card>
    );
}


export default Omregning