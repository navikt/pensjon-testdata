import React, {Component} from 'react';
import {Checkbox, Input, Select, SkjemaGruppe} from "nav-frontend-skjema";
import {Knapp} from "nav-frontend-knapper";

class Testcases extends Component {
    state = {
        isProcessing: false,
        testcases: [],
        selected: '',
        handlebars: [],
        fieldValues: {},
        opprettPerson: true
    };

    componentDidMount() {
        fetch('/api/testdata')
            .then(res => res.json())
            .then((data) => {
                this.setState({testcases: data.data})
            })
            .catch(console.log)
    }

    onChange = (event) => {
        this.setState({selected: event.target.value});

        if (event.target.value.length > 0) {
            fetch( '/api/testdata/handlebars/' + event.target.value)
                .then(res => res.json())
                .then((data) => {
                    this.setState({handlebars: data})
                })
                .catch(console.log)
        } else {
            this.setState({handlebars: [], fieldValues: {}})
        }
        console.log("Changed to: " + event.target.value);
    };

    checkboxChange = (event) => {
        this.setState({opprettPerson: event.target.checked});
    };

    lagre = (event) => {
        console.log("Lagre");
        this.setState({isProcessing : true})
        let body = {
            handlebars: this.state.fieldValues,
            testCaseId: this.state.selected,
            server: localStorage.getItem('pensjon-testdata-db-server'),
            database: localStorage.getItem('pensjon-testdata-db-database'),
            username: localStorage.getItem('pensjon-testdata-db-brukernavn'),
            password: localStorage.getItem('pensjon-testdata-db-passord'),
            opprettPerson: this.state.opprettPerson

        }
        fetch('/api/testdata', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body)
        }).finally((data) => {
                this.setState({isProcessing : false})
            });
    };

    fieldChangeHandler = (event) => {
        let name = event.target.name;
        let val = event.target.value;
        let copy = JSON.parse(JSON.stringify(this.state.fieldValues))
        copy[name] = val;
        this.setState({fieldValues: copy});
    };

    render() {
        return (
            <div style={{textAlign: 'left', width: '40%', maxWidth: '20rem', margin: '0 auto'}}>
                <Select bredde="xl" label='Velg scenario:' onChange={this.onChange}>
                    <option value=''>Velg</option>
                    {this.state.testcases.map((testcase) => (
                        <option value={testcase.navn} key={testcase.navn}>{testcase.navn}</option>
                    ))}
                </Select>
                <div>
                    {this.state.handlebars.map((field) => (
                        <Input style={{textAlign: 'left',}} bredde="XL" label={field.handlebar} name={field.handlebar} key={field.handlebar}
                               onChange={this.fieldChangeHandler}/>
                    ))}
                </div>
                <Checkbox label={'Opprett person'} checked={this.state.opprettPerson}  onChange={this.checkboxChange}/>
                <SkjemaGruppe>
                    { this.state.isProcessing ? <Knapp className="btn" spinner> </Knapp> : <Knapp className="btn" onClick={this.lagre}>Lagre</Knapp> }
                </SkjemaGruppe>
            </div>
        );
    }
}

export default Testcases