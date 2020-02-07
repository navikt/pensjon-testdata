import React, {useEffect} from 'react';
import {Fareknapp} from "nav-frontend-knapper";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions";
import Button from "@material-ui/core/Button";
import {SnackbarContext} from "../support/Snackbar";
import {callURL} from "../../util/rest";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import {makeStyles} from "@material-ui/core/styles";

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

const SlettTestdata = () => {
    const classes = useStyles();
    const [allowedClearDB, setallowedClearDB] = React.useState(false);
    const allowedToClearDatabase = async function () {
        const response = await fetch('/api/testdata/canclear/', {method: 'GET'});
        const allowed = await response.json();
        setallowedClearDB(JSON.parse(allowed));
    };

    useEffect(() => {
        allowedToClearDatabase();
    }, []);

    return (
        <div>
            {allowedClearDB ?
                <Card className={classes.card} variant="outlined">
                    <CardHeader title="Fjern all testdata fra database"/>
                    <CardContent>
                        Sletter alt innhold i database sett bort ifra kodeverk.
                    </CardContent>
                    <CardActions disableSpacing>
                        <SlettDialog/>
                    </CardActions>
                </Card>
                :
                ""}
        </div>
    );
};

const SlettDialog = () => {
    const [open, setOpen] = React.useState(false);
    const snackbarApi = React.useContext(SnackbarContext);

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const nullstill = (event) => {

        callURL(
            '/api/testdata/clear',
            'POST',
            {},
            () => {
                snackbarApi.openSnackbar('Nullstillt database!', 'success');
            },
            () => {
                snackbarApi.openSnackbar('Nullstilling av database feilet!', 'error');
            }
        ).finally(() => {
                setOpen(false);
            }
        );
    };

    return (
        <div>
            <Fareknapp className="btn" onClick={handleClickOpen}>Slett all testdata</Fareknapp>
            <Dialog
                open={open}
                onClose={handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">{"Fjerne testdata"}</DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Du vil slette <b>alt</b> innhold fra databasen<br/>
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary">
                        Avbryt
                    </Button>
                    <Fareknapp onClick={nullstill} color="primary">
                        Slett data
                    </Fareknapp>
                </DialogActions>
            </Dialog>
        </div>
    );
}

export default SlettTestdata

