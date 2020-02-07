import React from 'react';
import FlyttSak from "./FlyttSak";
import OpprettPerson from "./OpprettPerson";
import IverksetteVedtak from "./IverksetteVedtak";
import AttestereVedtak from "./AttestereVedtak";
import Grid from "@material-ui/core/Grid";
import {makeStyles} from '@material-ui/core/styles';
import OpprettInntekt from "./OpprettInntekt";
import SlettTestdata from "./SlettTestdata";

const useStyles = makeStyles(theme => ({
    root: {
        flexGrow: 1,

    },
    paper: {
        padding: theme.spacing(2),
        textAlign: 'center',
        color: theme.palette.text.secondary,
    },
}));

const TestVerktoy = () => {
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Grid container spacing={3} justify="center">
                <Grid item >
                    <OpprettPerson/>
                </Grid>
                <Grid item >
                    <FlyttSak/>
                </Grid>
                <Grid item >
                    <AttestereVedtak/>
                </Grid>
                <Grid item >
                    <IverksetteVedtak/>
                </Grid>
                <Grid item >
                    <OpprettInntekt/>
                </Grid>
                <Grid item >
                    <SlettTestdata/>
                </Grid>
            </Grid>
        </div>
    );
}
export default TestVerktoy