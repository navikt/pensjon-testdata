import React, {useEffect, useState} from "react";
import {createMuiTheme, MuiThemeProvider} from "@material-ui/core/styles";
import logo from './logo.png';
import './App.css';

import SlettTestdata from "./components/sletttestdata";
import OpprettPerson from "./components/opprett-person";
import HentTestdata from "./components/hent-testdata";
import Settings from "./components/settings";
import OpprettTestcase from "./components/opprett-testcase";
import Iverksett from "./components/iverksett";

import BottomNavigation from '@material-ui/core/BottomNavigation';
import BottomNavigationAction from '@material-ui/core/BottomNavigationAction';
import EqualizerIcon from '@material-ui/icons/Equalizer';
import PersonIcon from '@material-ui/icons/Person';
import InputIcon from '@material-ui/icons/Input';
import DeleteForeverIcon from '@material-ui/icons/DeleteForever';
import SettingsIcon from '@material-ui/icons/Settings';
import BuildIcon from '@material-ui/icons/Build';
import CssBaseline from "@material-ui/core/CssBaseline";

import { SnackbarContextProvider} from "./components/snackbar";





const App = () => {
    const [activeView, setActiveView] = useState('opprettPerson')
    const [enableClear, setEnableClear] = useState({enableClear: false});
    const [theme, setTheme] = useState({
        palette: {
            type: "light"
        }
    });

    const muiTheme = createMuiTheme(theme);

    useEffect(() => {
        evaluateMenu();
    }, []);

    const toggleDarkTheme = () => {
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
                <div className="App-header" style={{textAlign: 'center', width: '100%', margin: '0 auto'}}>
                    <img src={logo} className="App-logo" alt="logo"/>
                    <h2>NAV Pensjon testdata</h2>
                </div>
                <BottomNavigation showLabels>
                    <BottomNavigationAction label="Opprett person" icon={<PersonIcon/>}
                                            onClick={() => setActiveView('opprettPerson')}/>
                    <BottomNavigationAction label="Hente testdata" icon={<EqualizerIcon/>}
                                            onClick={() => setActiveView('henteTestdata')}/>
                    <BottomNavigationAction label="Opprett testdata" icon={<InputIcon/>}
                                            onClick={() => setActiveView('lagreTestdata')}/>
                    <BottomNavigationAction label="Testverktøy" icon={<BuildIcon/>}
                                            onClick={() => setActiveView('testverktoy')}/>

                    {enableClear ? <BottomNavigationAction label="Fjern testdata" icon={<DeleteForeverIcon/>}
                                                           onClick={() => setActiveView('slettTestdata')}/> : ""}
                    <BottomNavigationAction label="Instillinger" icon={<SettingsIcon/>}
                                            onClick={() => setActiveView('instillinger')}/>
                </BottomNavigation>

                <SnackbarContextProvider>
                    {activeView === 'opprettPerson' ? <OpprettPerson/> : <p></p>}
                    {activeView === 'henteTestdata' ? <HentTestdata/> : <p></p>}
                    {activeView === 'lagreTestdata' ? <OpprettTestcase/> : <p></p>}
                    {activeView === 'slettTestdata' ? <SlettTestdata/> : <p></p>}
                    {activeView === 'testverktoy' ? <Iverksett/> : <p></p>}
                    {activeView === 'instillinger' ? <Settings action={evaluateMenu} darkMode={toggleDarkTheme}/> :
                        <p></p>}
                </SnackbarContextProvider>
            </div>

        </MuiThemeProvider>
    );
}

export default App;


