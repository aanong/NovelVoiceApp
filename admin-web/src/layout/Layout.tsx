import React from 'react';
import { Layout, Menu } from 'antd';
import { Outlet, useNavigate } from 'react-router-dom';
import {
    UserOutlined,
    VideoCameraOutlined,
    UploadOutlined,
    BookOutlined,
    SettingOutlined
} from '@ant-design/icons';

const { Header, Content, Footer, Sider } = Layout;

const AppLayout: React.FC = () => {
    const navigate = useNavigate();

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Sider collapsible>
                <div className="demo-logo-vertical" style={{ height: 32, margin: 16, background: 'rgba(255, 255, 255, 0.2)' }} />
                <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline" items={[
                    {
                        key: '1',
                        icon: <BookOutlined />,
                        label: 'Novel Management',
                        onClick: () => navigate('/novel/list')
                    },
                    {
                        key: 'sub1',
                        icon: <SettingOutlined />,
                        label: 'System Management',
                        children: [
                            {
                                key: '2',
                                label: 'Role Management',
                                onClick: () => navigate('/system/role')
                            },
                            {
                                key: '3',
                                label: 'Menu Management',
                                onClick: () => navigate('/system/menu')
                            }
                        ]
                    }
                ]} />
            </Sider>
            <Layout>
                <Header style={{ padding: 0, background: '#fff' }} />
                <Content style={{ margin: '0 16px' }}>
                    <div style={{ padding: 24, minHeight: 360, background: '#fff', marginTop: 16 }}>
                        <Outlet />
                    </div>
                </Content>
                <Footer style={{ textAlign: 'center' }}>Novel Admin ©2026</Footer>
            </Layout>
        </Layout>
    );
};

export default AppLayout;
