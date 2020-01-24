import React, {useEffect, useState} from "react";
import {createMuiTheme, MuiThemeProvider} from "@material-ui/core/styles";
import logo from './logo.png';
import './App.css';
import HentTestdata from "./components/HentTestdata";
import OpprettTestdata from "./components/OpprettTestdata";
import TestVerktoy from "./components/TestVerktoy";

import BottomNavigation from '@material-ui/core/BottomNavigation';
import BottomNavigationAction from '@material-ui/core/BottomNavigationAction';
import EqualizerIcon from '@material-ui/icons/Equalizer';
import InputIcon from '@material-ui/icons/Input';
import DeleteForeverIcon from '@material-ui/icons/DeleteForever';
import BuildIcon from '@material-ui/icons/Build';
import CssBaseline from "@material-ui/core/CssBaseline";

import {SnackbarContextProvider} from "./components/Snackbar";
import SlettTestdata from "./components/SlettTestdata";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Switch from "@material-ui/core/Switch";

const App = () => {
    const [activeView, setActiveView] = useState('henteTestdata')
    const [enableClear, setEnableClear] = useState({enableClear: false});
    const [darkMode, setDarkMode] = useState(false);
    const [theme, setTheme] = useState({
        palette: {
            type: "light"
        }
    });

    const muiTheme = createMuiTheme(theme);

    useEffect(() => {
        evaluateMenu();
    }, []);

    const toggleDarkTheme = (event) => {
        setDarkMode(event.target.checked)
        let newPaletteType = theme.palette.type === "light" ? "dark" : "light";
        setTheme({
            palette: {
                type: newPaletteType
            }
        });
    };

    const evaluateMenu = function () {
        fetch('/api/testdata/canclear/', {
            method: 'GET'
        }).then(response => {
            console.log(response.clone().text())
            return response.json()
        }).then(json => {
            setEnableClear(JSON.parse(json));
        })
    };

    return (
        <MuiThemeProvider theme={muiTheme}>
            <CssBaseline/>
            <div>
                <div className="App-header" style={{textAlign: 'center', width: '100%'}}>
                    <img src={logo} className="App-logo" alt="logo"/>
                    <h2>Pensjon testdata</h2>
                </div>
                <div>
                    <BottomNavigation showLabels>
                        <BottomNavigationAction label="Hente testdata" icon={<EqualizerIcon/>}
                                                onClick={() => setActiveView('henteTestdata')}/>
                        <BottomNavigationAction label="Opprett testdata" icon={<InputIcon/>}
                                                onClick={() => setActiveView('lagreTestdata')}/>
                        <BottomNavigationAction label="Testverktøy" icon={<BuildIcon/>}
                                                onClick={() => setActiveView('testverktoy')}/>
                        {enableClear ? <BottomNavigationAction label="Fjern testdata" icon={<DeleteForeverIcon/>}
                                                               onClick={() => setActiveView('slettTestdata')}/> : ""}
                    </BottomNavigation>
                </div>
                <div>
                    <SnackbarContextProvider>
                        {activeView === 'henteTestdata' ? <HentTestdata/> : <p></p>}
                        {activeView === 'lagreTestdata' ? <OpprettTestdata/> : <p></p>}
                        {activeView === 'slettTestdata' ? <SlettTestdata/> : <p></p>}
                        {activeView === 'testverktoy' ? <TestVerktoy/> : <p></p>}
                    </SnackbarContextProvider>
                    <div style={{
                        position: "absolute",
                        bottom: 0,
                        right: 0
                    }}>
                        <FormControlLabel
                            control={
                                <Switch
                                    name={"developer"}
                                    checked={darkMode}
                                    onChange={toggleDarkTheme}
                                    color="primary"
                                    value={"developer"}
                                />
                            }
                            label="Dark mode"
                        />
                    </div>
                </div>
            </div>
        </MuiThemeProvider>
    );
}

export default App;


