import React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import {Fareknapp} from "nav-frontend-knapper";

export default function SlettDialog() {
    const [open, setOpen] = React.useState(false);
    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const nullstill = (event) => {
        console.log("Reset");
        let body ={
        };
        fetch('/api/testdata/clear', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body:  JSON.stringify(body)
        });
        setOpen(false);
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