import React from 'react';
import {Fareknapp} from "nav-frontend-knapper";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions";
import Button from "@material-ui/core/Button";
import {SnackbarContext} from "./Snackbar";
import {callURL} from "../util/rest";

const SlettTestdata = () => {
    return (
        <div style={{textAlign: 'left', width: '40%', maxWidth: '20rem', margin: '0 auto'}}>
            <SlettDialog/>
        </div>
    );
}

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
