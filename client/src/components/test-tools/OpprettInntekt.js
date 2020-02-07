import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import {SnackbarContext} from "../support/Snackbar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import {makeStyles} from '@material-ui/core/styles';
import CreditCardIcon from '@material-ui/icons/CreditCard';
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Switch from "@material-ui/core/Switch";

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

const OpprettInntekt = () => {
    const classes = useStyles();

    const [isProcessing, setIsProcessing] = useState(false);
    const [fnr, setFnr] = useState('');
    const [fomAar, setFomAar] = useState();
    const [tomAar, setTomAar] = useState();
    const [belop, setBelop] = useState(null);
    const [nedjusteringGrunnbelop, setNedjusteringGrunnbelop] = useState(true);

    const [fnrValidationText, setFnrValidationText] = useState('');
    const [fomAarValidationText, setFomAarValidationText] = useState('');
    const [tomAarValidationText, setTomAarValidationText] = useState('');
    const [belopValidationText, setBelopValidationText] = useState('');

    const snackbarApi = React.useContext(SnackbarContext);

    const resetValidation = () => {
        setFnrValidationText("");
        setFomAarValidationText("");
        setTomAarValidationText("");
        setBelopValidationText("");
    }

    const lagreInntekt = () => {
        resetValidation();
        if (!/^\d{11}$/.test(fnr.trim())) {
            setFnrValidationText("Må inneholde fnr på 11 siffer")
        }
        if (!/^\d{4}$/.test(fomAar)) {
            setFomAarValidationText("Må være årstall")
        }
        if (!/^\d{4}$/.test(tomAar)) {
            setTomAarValidationText("Må være årstall")
        }
        if (fomAar > tomAar) {
            setTomAarValidationText("Må være senere eller lik fom år.")
        }
        if (!/^\d+$/.test(belop)) {
            setBelopValidationText("Må inneholde tall")
        }
        if (isValidationError(fnrValidationText || isValidationError(fomAarValidationText) || isValidationError(tomAarValidationText) || isValidationError(belopValidationText))) {
            //NOOP
        } else {
            execute();
        }
    };

    const execute = async () => {
        setIsProcessing(true);
        const response = await fetch('/api/inntekt', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                fnr: fnr,
                fomAar: fomAar,
                tomAar: tomAar,
                belop: belop,
                redusertMedGrunnbelop: nedjusteringGrunnbelop
            })
        });

        const json = await response.json();

        if (response.status === 200) {
            snackbarApi.openSnackbar('Inntekter fom: ' + fomAar + ' tom: ' + tomAar + ' lagret', 'success');
        } else {
            snackbarApi.openSnackbar('Lagring av inntekt feilet: ' + json.message, 'error');
            console.log(json.message);
        }
        setIsProcessing(false);
    }

    const isValidationError = (value) => {
        return value.length !== 0
    }

    return (
        <Card className={classes.card} variant="outlined">
            <CardHeader title="Lagre inntekter"/>
            <CardContent>
                <FormControlLabel
                    control={
                        <Switch
                            name={"grunnbelop"}
                            checked={nedjusteringGrunnbelop}
                            onChange={(e) => setNedjusteringGrunnbelop(e.target.checked)}
                            color="primary"
                            value={"nedjusteringGrunnbelop"}
                        />
                    }
                    label="Nedjuster med grunnbelop"
                />
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Fødselsnummer"
                           name="fnr"
                           key="fnr"
                           variant="outlined"
                           helperText={fnrValidationText}
                           onChange={e => setFnr(e.target.value)}
                           error={isValidationError(fnrValidationText)}
                />
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Fom år"
                           name="fomAar"
                           key="fomAar"
                           variant="outlined"
                           helperText={fomAarValidationText}
                           onChange={e => setFomAar(e.target.value)}
                           error={isValidationError(fomAarValidationText)}
                />
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Tom år"
                           name="tomAar"
                           key="tomAar"
                           variant="outlined"
                           helperText={tomAarValidationText}
                           onChange={e => setTomAar(e.target.value)}
                           error={isValidationError(tomAarValidationText)}
                />
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Beløp"
                           name="belop"
                           key="belop"
                           variant="outlined"
                           helperText={belopValidationText}
                           onChange={e => setBelop(e.target.value)}
                           error={isValidationError(belopValidationText)}
                />
                <br/>
                {nedjusteringGrunnbelop === false ? "Beløp angis som fast årsbeløp for alle år." : "Beløp angis som årsbeløp i dagens kroneverdi, og vil nedjusteres basert på snitt grunnbeløp i inntektsåret."}

            </CardContent>
            <CardActions disableSpacing>
                <Button onClick={() => lagreInntekt()}
                        variant="contained"
                        disabled={isProcessing ? true : false}
                        startIcon={<CreditCardIcon/>}>
                    Lagre inntekter</Button>
            </CardActions>
        </Card>
    );
}

export default OpprettInntekt