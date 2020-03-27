import React, {useEffect, useState} from 'react';
import TextField from '@material-ui/core/TextField';
import AccountTreeIcon from '@material-ui/icons/AccountTree';
import Button from '@material-ui/core/Button';
import {SnackbarContext} from "../support/Snackbar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import {makeStyles} from '@material-ui/core/styles';
import {callURL} from "../../util/rest";
import Autocomplete from '@material-ui/lab/Autocomplete';

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

const FlyttSak = () => {
    const classes = useStyles();

    const [isProcessing, setIsProcessing] = useState(false);
    const [sakId, setSakId] = useState('');
    const [nyEnhetId, setNyEnhetId] = useState('');

    const [alleEnheter, setAlleEnheter] = useState([]);

    const snackbarApi = React.useContext(SnackbarContext);

    useEffect(() => {
        getEnheter();
    }, []);

    const getEnheter = async () => {
        const response = await fetch('/api/enheter', {method: 'GET'});
        const alleEnheter = await response.json();

        const unikealleEnheter = alleEnheter
            .filter((thing, index, self) =>
            index === self.findIndex((t) => t.place === thing.place && t.enhetNr === thing.enhetNr))
            .filter((element) => {
                return element.enhetNr !== undefined;
            });
        setAlleEnheter(unikealleEnheter);
    }

    const flyttEnhet = (event) => {
        setIsProcessing(true);
        console.log(nyEnhetId);
        callURL(
            '/api/flytte-sak',
            'POST',
            {
                sakId: sakId,
                nyEnhet: nyEnhetId
            },
            () => {
                snackbarApi.openSnackbar('Enhet flyttet', 'success');
            },
            () => {
                snackbarApi.openSnackbar('Flytting av enhet feilet!', 'error');
            }
        ).finally(() => {
                setIsProcessing(false);
            }
        );
    };

    return (
        <Card className={classes.card}>
            <CardHeader title="Flytt eierenhet for sak"/>
            <CardContent>
                <p>Flytter eiertilgang for en sak til en ny enhet. Åpne krav vil ikke bli flyttet over på den nye enheten.</p>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Saksnummer"
                           name="sakid"
                           key="sakid"
                           variant="outlined"
                           onChange={e => setSakId(e.target.value)}/><br/>
                <Autocomplete
                    id="nyEnhet"
                    options={alleEnheter}
                    getOptionLabel={option => option.enhetNr + " " + option.enhetNavn}
                    style={{width: 223 }}
                    onChange={(event, value) => setNyEnhetId(value !== null ? value.enhetNr:'')}
                    renderInput={params => (
                        <TextField {...params}
                                   label="Ny enhet"
                                   variant="outlined"
                                   fullWidth
                        />
                    )}
                />
            </CardContent>
            <CardActions>
                <Button onClick={() => flyttEnhet()}
                        variant="contained"
                        size="small"
                        startIcon={<AccountTreeIcon/>}
                        disabled={isProcessing ? true : false}>
                    Flytt til enhet</Button>
            </CardActions>
        </Card>
    );
}

export default FlyttSak