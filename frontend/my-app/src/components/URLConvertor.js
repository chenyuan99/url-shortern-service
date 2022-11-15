import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {useState} from "react";

function URLConvertor() {

    const [longURL, setLongURL] = useState("");
    const [shortURL, setShortURL] = useState("");

    function handleSubmit(event) {
        event.preventDefault();

        console.log(event.target.elements.long) // from elements property

        const long = event.target.elements.long
        const short = event.target.elements.short

        var formdata = new FormData(event.target);

        formdata.append("long_url", short.value);

        const data = Object.fromEntries(formdata.entries());
        console.dir(data);

        var requestOptions = {
            method: 'POST',
            body: formdata,
            redirect: 'follow',
        };


        // fetch("http://127.0.0.1:8000/api/shorten", requestOptions)
        //     .then(response => response.text())
        //     .then(result => console.log(result))
        //     .catch(error => console.log('error', error));

        // GET request using fetch with error handling
        fetch("/api/shorten", requestOptions)
            .then(response => {
                // console.log(response.text())
                response.text().then(result => {
                    console.log(result)
                    setLongURL(result)
                    // console.log(result)
                })


            })
            .catch(error => {
                console.log(error)
                console.error('There was an error!', error);
            });
    }


    return (
        <>
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formBasicShortURL">
                    <Form.Label>Short URL</Form.Label>
                    <Form.Control type="url" placeholder="Short URL" name="short"/>
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicLongURL">
                    <Form.Label>Long URL</Form.Label>
                    <Form.Control type="url" placeholder="Long URL"  name="long"/>
                </Form.Group>
                <Button variant="primary" type="submit">
                    Submit
                </Button>
            </Form>
            <h1>{longURL}</h1>
        </>

    );
}

export default URLConvertor;