import React, { useEffect, useState } from 'react';
import { Table } from 'antd';
import axios from 'axios';

const NovelList: React.FC = () => {
    const [data, setData] = useState([]);

    useEffect(() => {
        // Fetch data
        // axios.get('/api/novels/list').then(res => setData(res.data));
    }, []);

    const columns = [
        {
            title: 'Title',
            dataIndex: 'title',
            key: 'title',
        },
        {
            title: 'Author',
            dataIndex: 'author',
            key: 'author',
        },
    ];

    return (
        <div>
            <h2>Novel List</h2>
            <Table dataSource={data} columns={columns} />
        </div>
    );
};

export default NovelList;
