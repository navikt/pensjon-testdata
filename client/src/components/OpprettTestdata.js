import React, {useEffect, useState} from 'react';
import {Input} from "nav-frontend-skjema";
import {Knapp} from "nav-frontend-knapper";
import {SnackbarContext} from "./support/Snackbar";
import {useForm} from 'react-hook-form';
import {DataGrid} from '@material-ui/data-grid';
import Grid from "@material-ui/core/Grid";
import makeStyles from "@material-ui/core/styles/makeStyles";

const OpprettTestdata = () => {
    const [isProcessing, setIsProcessing] = useState(false);
    const [testcases, setTestcases] = useState([]);
    const [selected, setSelected] = useState('');
    const [handlebars, setHandlebars] = useState([]);
    const snackbarApi = React.useContext(SnackbarContext);

    useEffect(() => {
        fetch('/api/testdata')
            .then(res => res.json())
            .then((data) => {
                setTestcases(data.data);
            })
            .catch(console.log)
    }, []);

    const { register, handleSubmit} = useForm({
        mode: 'onSubmit',
        reValidateMode: 'onChange',
        defaultValues: {},
        resolver: undefined,
        context: undefined,
        criteriaMode: "firstError",
        shouldFocusError: true,
        shouldUnregister: true,
    });


    const onChange = (event) => {
        let scenarioId = event.rowIds[0];
        setSelected(scenarioId);
        console.log('chosen ' + scenarioId);
        fetch('/api/testdata/handlebars/' + scenarioId)
            .then(res => res.json())
            .then((data) => {
                setHandlebars(data)
            })
            .catch(console.log)
    };

    const lagre = async (event) => {
        setIsProcessing(true);
        const response = await fetch('/api/testdata', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                handlebars: event,
                testCaseId: selected
            })
        });

        if (response.status === 200) {
            snackbarApi.openSnackbar('Testcase opprettet!', 'success');
        } else if (response.status === 417) {
            const text = await response.text();
            snackbarApi.openSnackbar('Testcase ikke opprettet: ' + text, 'warning');
        } else {
            const json = await response.json();
            snackbarApi.openSnackbar('Opprettelse av testcase feilet: ' + json.message, 'error');
            console.log(json.message);
        }
        setIsProcessing(false);
    };

    async function validateHandlebar(handlebar, val) {
        const response = await fetch('/api/validation?handlebar=' + handlebar + '&value=' + val, {
            method: 'POST'
        });
        if (response.status !== 200) {
            const text = await response.text();
            snackbarApi.openSnackbar(text, 'warning');
            return false;
        }
        else{
            snackbarApi.closeSnackbar();
            return true;
        }
    }

    const handlebarValidate = async (val, handlebar, validator) => {
        for (const customValidator of validator) {
            if (customValidator === 'fnr'){
                if (val.length === 11){
                    return await validateHandlebar(handlebar, val);
                }
                else{
                    snackbarApi.openSnackbar('Et fødselsnummer må være 11-sifret', 'warning');
                    return false;
                }
            }
        }
        return true;
    };

    const columns = [
        { field: 'navn', headerName: 'Testscenario', flex: 1, headerClassName: 'header'},
        { field: 'begrensninger', headerName: 'Forutsettninger for testdata', flex: 1, headerClassName: 'header'},
        { field: 'saksType', headerName: 'Sakstype', flex: 1, headerClassName: 'header'}
    ];

    const useStyles = makeStyles({
        root: {
            '& .header': {
                fontSize: 20,
                backgroundColor: 'lightGrey',
            },
        },
    });

    const classes = useStyles();

    return (
        <form onSubmit={handleSubmit(lagre)} style={{width: '100%'}}>
            <Grid container spacing={3} justify="center" alignItems="flex-start" direction="row">
                <Grid item style={{width: '50%'}}>
                    <div className={classes.root}>
                    <DataGrid
                            rows={testcases} columns={columns} pageSize={5}
                            onSelectionChange={(newSelection) => {
                                onChange(newSelection);
                            }}
                            hideFooterSelectedRowCount
                            autoHeight
                        />
                    </div>
                </Grid>
                <Grid item style={{width: '20%'}}>
                    <div>
                        {handlebars.map((field) => (
                            <Input style={{textAlign: 'left',}} type={field.inputtype} bredde="XL" label={field.handlebar} name={field.handlebar}
                                   key={field.handlebar}
                                   inputRef={register({required: true,
                                       validate: async value => await handlebarValidate(value, field.handlebar, field.validators)})}
                            />
                        ))}
                        {isProcessing ?
                            <Knapp className="btn" spinner> </Knapp> :
                            selected ? <Knapp className="btn">Lagre</Knapp> : ''}
                    </div>
                </Grid>
            </Grid>
            </form>
    );
};

export default OpprettTestdata
