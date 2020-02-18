import React from 'react';
import OpprettPerson from "./test-tools/OpprettPerson";
import IverksetteVedtak from "./test-tools/IverksetteVedtak";
import AttestereVedtak from "./test-tools/AttestereVedtak";
import Grid from "@material-ui/core/Grid";
import {makeStyles} from '@material-ui/core/styles';
import SlettTestdata from "./test-tools/SlettTestdata";

const useStyles = makeStyles(theme => ({
    root: {
        flexGrow: 1,
        justify: "center"
    },
    paper: {
        padding: theme.spacing(2),
        textAlign: 'center',
        color: theme.palette.text.secondary,
    },
}));

const UtviklerVerktoy = () => {
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Grid container spacing={3} justify="center"   alignItems="center"
                  direction="row">
                <Grid item >
                    <OpprettPerson/>
                </Grid>
                <Grid item >
                    <AttestereVedtak/>
                </Grid>
                <Grid item >
                    <IverksetteVedtak/>
                </Grid>
                <Grid item >
                    <SlettTestdata/>
                </Grid>
            </Grid>
        </div>
    );
}
export default UtviklerVerktoy