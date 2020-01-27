export const callURL = (url,
                      method,
                      body,
                      successCallback,
                      errorCallback) => {

    let status = null;

    return fetch(url, {
        method: method,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(body)
    }).then(response => {
        status = response.status;
        return response.json();
    }).then(json => {
        if (status === 200) {
            successCallback();
        } else {
            console.log(json.message);
            errorCallback();
        }
        return json;
    })
}