import React, {Component} from 'react';
import Snackbar from "@material-ui/core/Snackbar";
import Alert from "@material-ui/lab/Alert";

export const SnackbarContext = React.createContext();

    export class SnackbarContextProvider extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isOpen: false,
            message: '',
            type: 'error'
        };
    }

    openSnackbar = (message,type) => {
        this.setState({
            message,
            isOpen: true,
            type: type
        });
    };

    closeSnackbar = () => {
        this.setState({
            message: '',
            isOpen: false,
        });
    };

    render() {
        const {children} = this.props;
        return (
            <SnackbarContext.Provider value={{
                openSnackbar: this.openSnackbar,
                closeSnackbar: this.closeSnackbar,
                snackbarIsOpen: this.state.isOpen,
                message: this.state.message,
                type: this.state.type
            }}>
               <SharedSnackbar/>
                {children}
            </SnackbarContext.Provider>
        )
    }
}

const SnackbarConsumer = SnackbarContext.Consumer;

const SharedSnackbar = () => (
    <SnackbarConsumer>
        {({ snackbarIsOpen, message, closeSnackbar, type }) => (
            <Snackbar
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'left',
                }}
                open={snackbarIsOpen}
                autoHideDuration={6000}
                onClose={closeSnackbar}
                >
                <Alert onClose={closeSnackbar} variant="filled" severity={type}>
                    {message}
                </Alert>
            </Snackbar>
        )}
    </SnackbarConsumer>
);

