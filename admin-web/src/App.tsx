import { Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import AppLayout from './layout/Layout';
import RoleList from './pages/system/RoleList';
import MenuList from './pages/system/MenuList';
import NovelList from './pages/novel/NovelList';

function App() {
    return (
        <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/" element={<AppLayout />}>
                <Route path="system/role" element={<RoleList />} />
                <Route path="system/menu" element={<MenuList />} />
                <Route path="novel/list" element={<NovelList />} />
                <Route index element={<div>Welcome to Novel Admin</div>} />
            </Route>
        </Routes>
    )
}

export default App
