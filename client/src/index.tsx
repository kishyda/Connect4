import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter, Route, Routes } from 'react-router';
import Login from './Login/Login';
import LocalPvPGameBoard from './Board/LocalPvPGame/LocalPvPGame';
import OnlinePvPGame from './Board/OnlinePvPGame/OnlinePvPGame';
import OfflineAIGame from './Board/PvAIGame/PvAIGame';

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);
root.render(
    <BrowserRouter>
        <Routes>
            <Route path="/" element={<App />} />
            <Route path="/login" element={<Login />} />
            <Route path="/localpvpgame" element={<LocalPvPGameBoard />} />
            <Route path="/onlinepvpgame" element={<OnlinePvPGame />} />
            <Route path="/pvaigame" element={<OfflineAIGame />} />
        </Routes>
    </BrowserRouter>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
