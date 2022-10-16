import logo from './logo.svg';
import './App.css';
import URLConvertor from "./components/URLConvertor";
import 'bootstrap/dist/css/bootstrap.min.css';
import {Container} from "react-bootstrap";
import Header from "./components/Header";

function App() {
    return (
        <div className="App">
            <Container>
                <Header/>
                <URLConvertor/>
            </Container>
        </div>
    );
}

export default App;
