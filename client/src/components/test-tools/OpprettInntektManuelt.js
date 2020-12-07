import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import {SnackbarContext} from "../support/Snackbar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import Button from '@material-ui/core/Button';
import {makeStyles} from '@material-ui/core/styles';
import CreditCardIcon from '@material-ui/icons/CreditCard';
import {Input} from "nav-frontend-skjema";
import {Knapp} from "nav-frontend-knapper";
import {useForm} from "react-hook-form";

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

const OpprettInntektManuelt = () => {
    const classes = useStyles();

    const [isProcessing, setIsProcessing] = useState(false);
    const [skjemahentet, setSkjemahentet] = useState(false);
    const [fnr, setFnr] = useState('');
    const [fomAar, setFomAar] = useState();
    const [tomAar, setTomAar] = useState();

    const [fnrValidationText, setFnrValidationText] = useState('');
    const [fomAarValidationText, setFomAarValidationText] = useState('');
    const [tomAarValidationText, setTomAarValidationText] = useState('');

    const [handlebars, setHandlebars] = useState([]);


    const snackbarApi = React.useContext(SnackbarContext);

    const {register, handleSubmit} = useForm({
        mode: 'onSubmit',
        reValidateMode: 'onChange',
        defaultValues: {},
        resolver: undefined,
        context: undefined,
        criteriaMode: "firstError",
        shouldFocusError: true,
        shouldUnregister: true,
    });

    const resetValidation = () => {
        setFnrValidationText("");
        setFomAarValidationText("");
        setTomAarValidationText("");
    }

    const validerOgHentSkjema = () => {
        console.log("Process hent-inntektskjema")
        resetValidation();
        // if (!/^\d{11}$/.test(fnr.trim())) //{ isNotValid(fnrValidationText) ||
        //     setFnrValidationText("Må inneholde fnr på 11 siffer")
        // }
        if (!/^\d{4}$/.test(fomAar)) {
            setFomAarValidationText("Må være årstall")
        }
        if (!/^\d{4}$/.test(tomAar)) {
            setTomAarValidationText("Må være årstall")
        }
        if (isNotValid(fomAarValidationText) || isNotValid(tomAarValidationText)) {
            snackbarApi.openSnackbar('Feil i validering, inntekt ble ikke lagret', 'error');
        } else {
            console.log("Henter inntektskjema hæ")
            hentInntektSkjema();
        }
    };

    const hentInntektSkjema = async () => {
        setIsProcessing(true);
        var range = Array(tomAar - fomAar + 1)
            .fill(fomAar)
            .map((x, y) => Number(x) + y)
            .map(x => {
                return {aar: x, inntekt: 0}
            })

        setHandlebars(range);
        setIsProcessing(false);
        setSkjemahentet(true);
    }

    const lagre = async (inntekter) => {
        setIsProcessing(true);
        const response = await fetch('/api/inntektskjema', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                fnr: fnr,
                inntekter: Object.keys(inntekter).map((aar) => ({aar: aar, inntekt: inntekter[aar]}))
            })
        });

        if (response.status === 200) {
            let json = await response.json();
            snackbarApi.openSnackbar('Alle inntekter ble lagret! ' + JSON.stringify(json), 'success');
            setSkjemahentet(false);
        } else {
            let json = await response.json();
            snackbarApi.openSnackbar('Lagring av inntekt feilet: ' + json.message, 'error');
            console.log(json);
        }
        setIsProcessing(false);
    };

    const isNotValid = (value) => {
        return value.length !== 0
    }

    return (
        <Card className={classes.card} variant="outlined">
            <CardHeader title="Lagre ulike inntekter"/>
            <CardContent>
                <p>Oppretter varierte inntekter for brukere ved å benytte grensesnitt mot POPP</p>
                {skjemahentet ?
                    <form onSubmit={handleSubmit(lagre)} style={{width: '100%'}}>
                        <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                                   label="Fødselsnummer"
                                   name="fnr"
                                   key="fnr"
                                   variant="outlined"
                                   helperText={fnrValidationText}
                                   onChange={e => setFnr(e.target.value)}
                                   error={isNotValid(fnrValidationText)}
                        />
                        <div>
                            {handlebars.map((field) => (
                                <Input style={{textAlign: 'left',}} type="number" bredde="XL" label={String(field.aar)}
                                       name={String(field.aar)}
                                       key={field.aar}
                                       defaultValue={field.inntekt}
                                       inputRef= {register({required: true})}
                                />
                            ))}
                            {isProcessing ? <Knapp className="btn" spinner> </Knapp> : <Knapp className="btn">Lagre inntekter</Knapp>}
                        </div>
                    </form>
                    :
                    <div>
                        <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                                   label="Fom år"
                                   name="fomAar"
                                   key="fomAar"
                                   variant="outlined"
                                   helperText={fomAarValidationText}
                                   onChange={e => setFomAar(e.target.value)}
                                   error={isNotValid(fomAarValidationText)}
                        />
                        <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                                   label="Tom år"
                                   name="tomAar"
                                   key="tomAar"
                                   variant="outlined"
                                   helperText={tomAarValidationText}
                                   onChange={e => setTomAar(e.target.value)}
                                   error={isNotValid(tomAarValidationText)}
                        />
                    </div>
                }
           </CardContent>
            <CardActions disableSpacing>
                <Button onClick={() => validerOgHentSkjema()}
                        variant="contained"
                        disabled={isProcessing || skjemahentet}
                        startIcon={<CreditCardIcon/>}>
                    Hent skjema</Button>
            </CardActions>
        </Card>
    );
}

export default OpprettInntektManuelt