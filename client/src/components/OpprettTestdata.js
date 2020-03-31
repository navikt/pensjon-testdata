import React, {useEffect, useState} from 'react';
import {Input, Select} from "nav-frontend-skjema";
import {Knapp} from "nav-frontend-knapper";
import {SnackbarContext} from "./support/Snackbar";

const OpprettTestdata = () => {
    const [isProcessing, setIsProcessing] = useState(false);
    const [testcases, setTestcases] = useState([]);
    const [selected, setSelected] = useState('');
    const [handlebars, setHandlebars] = useState([]);
    const [fieldValues, setFieldValues] = useState({});
    const [limitations, setLimitations] = useState([]);
    const [description, setDescription] = useState('');
    const snackbarApi = React.useContext(SnackbarContext);

    useEffect(() => {
        fetch('/api/testdata')
            .then(res => res.json())
            .then((data) => {
                setTestcases(data.data);
            })
            .catch(console.log)
    }, []);


    const onChange = (event) => {
        setSelected(event.target.value);
        if (event.target.value.length > 0) {
            setDescription(
                testcases.filter(testcase => event.target.value === testcase.navn)
                    .map(testcase => testcase.fritekstbeskrivelse)[0]);

            setLimitations(
                testcases.filter(testcase => event.target.value === testcase.navn)
                    .filter(testcase => testcase.begrensninger.length > 0)
                    .map(testcase => testcase.begrensninger));

            fetch('/api/testdata/handlebars/' + event.target.value)
                .then(res => res.json())
                .then((data) => {
                    setHandlebars(data)
                })
                .catch(console.log)
        } else {
            setHandlebars([]);
            setFieldValues([]);
            setLimitations([]);
            setDescription('');
        }
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
                handlebars: fieldValues,
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
                {description ?
                    <div><b>Beskrivelse av scenario:</b><br/><ul style={{listStyleType: 'none',}}><li>{description}</li></ul></div> :
                    <div/>
                }
                {limitations.length > 0 ?
                    <div><b>Forutsettninger for testdata:</b><br/><ul>{limitations.map((field) =>(<li>{field}</li>))}</ul></div> :
                    <div/>
                }
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