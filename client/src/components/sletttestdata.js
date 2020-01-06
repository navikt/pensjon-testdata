import React, {Component} from 'react';
import {SkjemaGruppe} from "nav-frontend-skjema";
import SlettDialog from "./slett-dialog";

class SlettTestdata extends Component {
    state = {
        isProcessingNullstill: false,
        didComplete: true
    };

    render() {
        return (

            <div style={{textAlign: 'left', width: '40%', maxWidth: '20rem', margin: '0 auto'}}>
                <SkjemaGruppe>
                    <SlettDialog/>
                </SkjemaGruppe>

            </div>
        );
    }
}

export default SlettTestdata

