import React, {useEffect, useState} from 'react';
import {Input, Select} from "nav-frontend-skjema";
import {Knapp} from "nav-frontend-knapper";
import {SnackbarContext} from "./Snackbar";
import {callURL} from "../util/rest";

const OpprettTestdata = () => {
    const [isProcessing, setIsProcessing] = useState(false);
    const [testcases, setTestcases] = useState([]);
    const [selected, setSelected] = useState('');
    const [handlebars, setHandlebars] = useState([]);
    const [fieldValues, setFieldValues] = useState({});

    const snackbarApi = React.useContext(SnackbarContext);

    useEffect(() => {
        fetch('/api/testdata')
            .then(res => res.json())
            .then((data) => {
                setTestcases(data.data);
            }).catch(console.log)
    }, []);


    const onChange = (event) => {
        setSelected(event.target.value);
        if (event.target.value.length > 0) {
            fetch('/api/testdata/handlebars/' + event.target.value)
                .then(res => res.json())
                .then((data) => {
                    setHandlebars(data)
                })
                .catch(console.log)
        } else {
            setHandlebars([]);
            setFieldValues([]);
        }
    };

    const lagre = (event) => {
        setIsProcessing(true);
        callURL(
            '/api/testdata',
            'POST',
            {
                handlebars: fieldValues,
                testCaseId: selected
            },
            () => {
                snackbarApi.openSnackbar('Testcase opprettet!', 'success');
            },
            () => {
                snackbarApi.openSnackbar('Lagring av testcase feilet!', 'error');
            }
        ).finally(() => {
                setIsProcessing(false);
            }
        );
    };

    const fieldChangeHandler = (event) => {
        let name = event.target.name;
        let val = event.target.value.trim();
        let copy = JSON.parse(JSON.stringify(fieldValues))
        copy[name] = val;
        setFieldValues(copy);
    };

    return (
        <div style={{textAlign: 'left', width: '40%', maxWidth: '20rem', margin: '0 auto'}}>
            <Select bredde="xl" label='Velg scenario:' onChange={e => onChange(e)}>
                <option value=''>Velg</option>
                {testcases.map((testcase) => (
                    <option value={testcase.navn} key={testcase.navn}>{testcase.navn}</option>
                ))}
            </Select>
            <div>
                {handlebars.map((field) => (
                    <Input style={{textAlign: 'left',}} bredde="XL" label={field.handlebar} name={field.handlebar}
                           key={field.handlebar}
                           onChange={e => fieldChangeHandler(e)}/>
                ))}
            </div>

            {isProcessing ?
                <Knapp className="btn" spinner> </Knapp> :
                <Knapp className="btn" onClick={e => lagre(e)}>Lagre</Knapp>}

        </div>
    );
}
export default OpprettTestdata