import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

function URLConvertor() {
    return (
        <Form>
            <Form.Group className="mb-3" controlId="formBasicShortURL">
                <Form.Label>Short URL</Form.Label>
                <Form.Control type="url" placeholder="Short URL" />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicLongURL">
                <Form.Label>Long URL</Form.Label>
                <Form.Control type="url" placeholder="Long URL" />
            </Form.Group>
            <Button variant="primary" type="submit">
                Submit
            </Button>
        </Form>
    );
}

export default URLConvertor;