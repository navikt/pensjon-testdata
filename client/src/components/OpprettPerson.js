import React, {useEffect, useState} from 'react';
import TextField from '@material-ui/core/TextField';
import AccountTreeIcon from '@material-ui/icons/AccountTree';
import Button from '@material-ui/core/Button';
import {SnackbarContext} from "./Snackbar";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import {makeStyles} from '@material-ui/core/styles';
import CircularProgress from "@material-ui/core/CircularProgress";
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


const OpprettPerson = () => {
    const classes = useStyles();

    const [isProcessing, setIsProcessing] = useState(false);
    const [fnr, setFnr] = useState('');

    const snackbarApi = React.useContext(SnackbarContext);

    const lagrePerson = (event) => {
        setIsProcessing(true);
        callURL(
            '/api/person/' + fnr,
            'POST',
            '',
            () => {snackbarApi.openSnackbar('Person opprettet!', 'success');},
            () => {snackbarApi.openSnackbar('Lagring av person feilet!', 'error');}
        ).then(() => {
                setIsProcessing(false);
            }
        );
    };

    return (
        <Card className={classes.card}>
            <CardHeader title="Opprett person"/>
            <CardContent>
                <TextField label="Fnr"
                           name="fnr"
                           key="fnr"
                           variant="outlined"
                           onChange={e => setFnr(e.target.value.trim())}/><br/><br/>
            </CardContent>
            <CardActions>
                {isProcessing ? <CircularProgress/> :
                    <Button variant="contained" onClick={() => lagrePerson()}>Lagre</Button>}
            </CardActions>
        </Card>
    );
}


export default OpprettPerson