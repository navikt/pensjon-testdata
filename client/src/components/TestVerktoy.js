import React from 'react';
import FlyttSak from "./test-tools/FlyttSak";
import Grid from "@material-ui/core/Grid";
import {makeStyles} from '@material-ui/core/styles';
import OpprettInntekt from "./test-tools/OpprettInntekt";
import BestillBrev from "./test-tools/BestillBrev";
import Omregning from "./test-tools/Omregning";
import OpprettInntektManuelt from "./test-tools/OpprettInntektManuelt"

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

const TestVerktoy = () => {
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <Grid container spacing={3} justify="center"   alignItems="flex-start" direction="row">
                <Grid item >
                    <OpprettInntekt/>
                </Grid>
                <Grid item >
                    <BestillBrev/>
                </Grid>
                <Grid item >
                    <Omregning/>
                </Grid>
                <Grid item >
                    <FlyttSak/>
                </Grid>
                <Grid item>
                    <OpprettInntektManuelt/>
                </Grid>
            </Grid>
        </div>
    );
}
export default TestVerktoy