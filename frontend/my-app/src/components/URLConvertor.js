import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import {useState} from "react";
import {Table} from "react-bootstrap";

function URLConvertor() {

    const [longURL, setLongURL] = useState("");
    const [shortURL, setShortURL] = useState("");
    const [expiredDate, setExpiredDate] = useState("");

    function handleSubmit(event) {
        event.preventDefault();

        console.log(event.target.elements.long) // from elements property

        const long = event.target.elements.long
        const short = event.target.elements.short


        var formdata = new FormData(event.target);

        if (long.value) {
            formdata.append("long_url", long.value);
        } else {
            formdata.append("short_url", short.value);
        }

        const data = Object.fromEntries(formdata.entries());
        console.dir(data);

        var requestOptions = {
            method: 'POST',
            body: formdata,
            redirect: 'follow',
        };


        // GET request using fetch with error handling
        if (long.value) {
            fetch("/api/shorten", requestOptions)
                .then(response => {
                    response.json().then(result => {
                        console.log(result["shorten_url"])
                        console.log(result["expired date"])
                        setShortURL(result["shorten_url"])
                        setExpiredDate((result["expired date"]))

                    })


                })
                .catch(error => {
                    console.log(error)
                    console.error('There was an error!', error);
                });
        } else {
            // GET request using fetch with error handling
            fetch("/api/resolve", requestOptions)
                .then(response => {
                    response.json().then(result => {
                        console.log(result["resolved_url"])
                        setLongURL(result["resolved_url"])

                    })


                })
                .catch(error => {
                    console.log(error)
                    console.error('There was an error!', error);
                });
        }
    }


    return (
        <>
            <Form onSubmit={handleSubmit}>

                <Form.Group className="mb-3" controlId="formBasicLongURL">
                    <Form.Label>Long URL</Form.Label>
                    <Form.Control type="url" placeholder="Long URL" name="long"/>
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicShortURL">
                    <Form.Label>Short URL</Form.Label>
                    <Form.Control type="url" placeholder="Short URL" name="short"/>
                </Form.Group>


                <Button variant="primary" type="submit">
                    Submit
                </Button>
            </Form>
            <Table>

                <thead>
                <tr>
                    <th>Long URL</th>
                    <th>Short URL</th>
                    <th>Expired Date</th>
                </tr>
                </thead>

                <tbody>
                <tr>
                    <td>{longURL}</td>
                    <td>{shortURL}</td>
                    <td>{expiredDate}</td>
                </tr>
                </tbody>
            </Table>
        </>

    );
}

export default URLConvertor;