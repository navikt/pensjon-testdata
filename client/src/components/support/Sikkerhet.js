import React from 'react';
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions";
import Button from "@material-ui/core/Button";
import SecurityIcon from '@material-ui/icons/Security';
import Dialog from "@material-ui/core/Dialog";
import IconButton from "@material-ui/core/IconButton";
import TextField from "@material-ui/core/TextField";

const Sikkerhet = () => {
    const [oidc, setOidc] = React.useState('');
    const [open, setOpen] = React.useState(false);

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const lagreOidcToken = () => {
        sessionStorage.setItem("ptbOidcToken", oidc);
        setOpen(false);
    };

    return (
        <div>
            <IconButton color="primary" onClick={() => handleClickOpen()}>
                <SecurityIcon />
            </IconButton>
            <Dialog
                open={open}
                onClose={handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">{"Sikkerhet"}</DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Lagring av OIDC token
                    </DialogContentText>
                    <TextField
                        id="outlined-multiline-static"
                        label="OIDC token"
                        multiline
                        rows="5"
                        defaultValue=""
                        variant="outlined"
                        style={{width: 450 }}
                        onChange={(e) => setOidc(e.target.value)}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary">
                        Avbryt
                    </Button>
                    <Button onClick={lagreOidcToken} color="primary">
                        Lagre
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
}

export default Sikkerhet