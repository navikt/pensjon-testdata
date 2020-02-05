import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import {SnackbarContext} from "./Snackbar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import {makeStyles} from '@material-ui/core/styles';
import CreditCardIcon from '@material-ui/icons/CreditCard';
import {callURL} from "../util/rest";

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
    const [belop, setBelop] = useState(0);


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
            return;
        }
        if (!/^\d{4}$/.test(fomAar)) {
            setFomAarValidationText("Må være årstall")
            return;
        }
        if (!/^\d{4}$/.test(tomAar)) {
            setTomAarValidationText("Må være årstall")
            return;
        }
        if (!/^\d+$/.test(belop)) {
            setBelopValidationText("Kan bare inneholde tall")
            return;
        }

        setIsProcessing(true);
        execute();
    };

    const execute = async () => {
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
                belop: belop
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

    return (
        <Card className={classes.card} variant="outlined">
            <CardHeader title="Lagre inntekter"/>
            <CardContent>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Fødselsnummer"
                           name="fnr"
                           key="fnr"
                           variant="outlined"
                           helperText={fnrValidationText}
                           onChange={e => setFnr(e.target.value)}/>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Fom år"
                           name="fomAar"
                           key="fomAar"
                           variant="outlined"
                           helperText={fomAarValidationText}
                           onChange={e => setFomAar(e.target.value)}/>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Tom år"
                           name="tomAar"
                           key="tomAar"
                           variant="outlined"
                           helperText={tomAarValidationText}
                           onChange={e => setTomAar(e.target.value)}/>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Beløp"
                           name="belop"
                           key="belop"
                           variant="outlined"
                           helperText={belopValidationText}
                           onChange={e => setBelop(e.target.value)}/>
            </CardContent>
            <CardActions disableSpacing>
                <Button onClick={() => lagreInntekt()}
                        variant="contained"
                        disabled={isProcessing ? true : false}
                        startIcon={<CreditCardIcon/>}>
                    Lagre inntekt</Button>
            </CardActions>
        </Card>
    );
}


export default OpprettInntekt