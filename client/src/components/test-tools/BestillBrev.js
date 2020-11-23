import React, {useEffect, useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import {SnackbarContext} from "../support/Snackbar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import {makeStyles} from '@material-ui/core/styles';
import MailIcon from '@material-ui/icons/Mail';
import Autocomplete from '@material-ui/lab/Autocomplete';


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


const BestillBrev = () => {
    const classes = useStyles();

    const [isProcessing, setIsProcessing] = useState(false);
    const [brevkoder, setBrevkoder] = useState([]);
    const [valgtBrev, setValgtBrev] = useState('');
    const [valgtBrevValidationText, setValgtBrevValidationText] = useState('');
    const [bruker, setBruker] = useState('');
    const [brukerValidationText, setBrukerValidationText] = useState('');
    const [sakId, setSakId] = useState('');
    const [sakIdValidationText, setSakIdValidationText] = useState('');
    const [kravId, setKravId] = useState('');
    const [kravIdValidationText, setKravIdValidationText] = useState('');
    const [vedtakId, setVedtakId] = useState('');
    const [vedtakIdValidationText, setVedtakIdValidationText] = useState('');

    const snackbarApi = React.useContext(SnackbarContext);

    useEffect(() => {
        getBrevkoder();
    }, []);

    const getBrevkoder = async () => {
        const response = await fetch('/api/brev/', {method: 'GET'});
        const alleBrev = await response.json();
        setBrevkoder(alleBrev);
    }

    const resetValidation = () => {
        setValgtBrevValidationText('');
        setBrukerValidationText('');
        setSakIdValidationText('');
        setKravIdValidationText('');
        setVedtakIdValidationText('');
    }

    const bestillBrev = async () => {
        resetValidation();
        if (valgtBrev === '') {
            setValgtBrevValidationText("Brev må velges")
        }
        if (!/^\d{11}$/.test(bruker.trim())) {
            setBrukerValidationText("Må inneholde fnr på 11 siffer")
        }
        if (!/^\d*$/.test(sakId.trim())) {
            setSakIdValidationText("Må inneholde tall")
        }
        if (!/^\d*$/.test(kravId.trim())) {
            setKravIdValidationText("Må inneholde tall")
        }
        if (!/^\d*$/.test(vedtakId.trim())) {
            setVedtakIdValidationText("Må inneholde tall")
        }

        if (isNotValid(valgtBrevValidationText) || isNotValid(brukerValidationText) || isNotValid(sakIdValidationText) || isNotValid(kravIdValidationText) || isNotValid(vedtakIdValidationText)) {
            snackbarApi.openSnackbar('Feil i validering, brev ble ikke bestilt', 'error');
        } else {
            execute();
        }


    }

    const execute = async () => {
        setIsProcessing(true);
        const response = await fetch('/api/brev', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                gjelder: bruker.trim(),
                internBatchBrevkode: valgtBrev.trim(),
                kravId: kravId.trim(),
                mottaker: bruker.trim(),
                sakId: sakId.trim(),
                vedtakId: vedtakId.trim()
            })
        });
        const json = await response.json();

        if (response.status === 200) {
            snackbarApi.openSnackbar('Brev bestillt', 'success');
        } else {
            snackbarApi.openSnackbar('Bestilling av brev feilet' + json.message, 'error');
            console.log(json.message);
        }

        setIsProcessing(false);
    }

    const isNotValid = (value) => {
        return value.length !== 0
    }

    return (
        <Card className={classes.card}>
            <CardHeader title="Bestill brev"/>
            <CardContent>
                <p>Benytter tjenestelaget til PEN for å bestille automatiske brev, som ordinært bestilles av batcher og automatiske prosesser.</p>
                <p>Merk at for brev som normalt bestilles av følgende batcher <i>BPEN002, BPEN006, BPEN010, BPEN015, BPEN030 og BPEN056</i> er man avhengig av at nødvendig brevinformasjon er bygget opp i testdata før brevet bestilles her.</p>
                <Autocomplete
                    id="Brev"
                    options={brevkoder}
                    getOptionLabel={option => option.kodeverdi + " : " + option.dekode + (option.dokumentmalId ? " (dokumentmalId " + option.dokumentmalId + ")" : "") }

                    style={{width: 300}}
                    onChange={(event, value) => setValgtBrev(value !== null ? value.kodeverdi:'')}
                    renderInput={params => (
                        <TextField {...params}
                                   label="Brev"
                                   variant="outlined"
                                   fullWidth
                                   error={isNotValid(valgtBrevValidationText)}
                        />
                    )}
                />
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px', width: 300}}
                           label="Bruker (fnr)"
                           name="bruker"
                           key="bruker"
                           variant="outlined"
                           helperText={brukerValidationText}
                           onChange={e => setBruker(e.target.value)}
                           error={isNotValid(brukerValidationText)}
                /><br/>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px', width: 300}}
                           label="Saksnummer"
                           name="sakid"
                           key="sakId"
                           variant="outlined"
                           helperText={sakIdValidationText}
                           onChange={e => setSakId(e.target.value)}
                           error={isNotValid(sakIdValidationText)}
                /><br/>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px', width: 300}}
                           label="KravId"
                           name="kravId"
                           key="kravId"
                           variant="outlined"
                           helperText={kravIdValidationText}
                           onChange={e => setKravId(e.target.value)}
                           error={isNotValid(kravIdValidationText)}
                /><br/>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px', width: 300}}
                           label="Vedtaks-ID"
                           name="vedtakid"
                           key="vedtakId"
                           variant="outlined"
                           helperText={vedtakIdValidationText}
                           onChange={e => setVedtakId(e.target.value)}
                           error={isNotValid(vedtakIdValidationText)}
                />
            </CardContent>
            <CardActions>
                <Button onClick={() => bestillBrev()}
                        variant="contained"
                        disabled={isProcessing ? true : false}
                        startIcon={<MailIcon/>}>
                    Bestill brev
                </Button>
            </CardActions>
        </Card>
    );
}


export default BestillBrev