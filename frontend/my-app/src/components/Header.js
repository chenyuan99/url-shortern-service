import {Container, Nav, Navbar} from "react-bootstrap";

import {NavLink} from "react-router-dom";


const navigation = [
    {name: 'Employees', href: '/Employees'},
    {name: 'Customers', href: '/Customers'},
];

function Header(props) {
    return (
        <>
            <Navbar bg="light" variant="light">
                <Container>
                    <Navbar.Brand href="/">Navbar</Navbar.Brand>
                    <Nav className="me-auto">
                        <Nav.Link href="/">Home</Nav.Link>
                        <Nav.Link href="/Employees">Employees</Nav.Link>
                        <Nav.Link href="/Customers">Customers</Nav.Link>
                    </Nav>
                </Container>
            </Navbar>

        </>

    );
}

export default Header;