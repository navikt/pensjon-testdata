import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import AccountTreeIcon from '@material-ui/icons/AccountTree';
import Button from '@material-ui/core/Button';
import {SnackbarContext} from "./Snackbar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import {makeStyles} from '@material-ui/core/styles';

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

    const snackbarApi = React.useContext(SnackbarContext);

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
        <Card className={classes.card}>
            <CardHeader title="Flytt eierenhet for sak" />
            <CardContent>
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="SakId"
                           name="sakid"
                           key="sakid"
                           variant="outlined"
                           onChange={e => setSakId(e.target.value)}/><br />
                <TextField style={{textAlign: 'left', marginBottom: '10px', marginTop: '10px'}}
                           label="Ny enhet"
                           name="nyEnhet"
                           key="nyEnhet"
                           variant="outlined"
                           onChange={e => setNyEnhetId(e.target.value)}/>
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