import 'react-native-gesture-handler';
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import LoginScreen from './src/screens/LoginScreen';
import RegisterScreen from './src/screens/RegisterScreen';
import NovelListScreen from './src/screens/NovelListScreen';
import NovelReaderScreen from './src/screens/NovelReaderScreen';
import ChatScreen from './src/screens/ChatScreen';

const Stack = createStackNavigator();

/**
 * 主应用组件
 * 配置导航路由
 */
function App(): JSX.Element {
    return (
        <NavigationContainer>
            <Stack.Navigator
                initialRouteName="Login"
                screenOptions={{
                    headerStyle: {
                        backgroundColor: '#f5f5dc',
                    },
                    headerTintColor: '#8b4513',
                    headerTitleStyle: {
                        fontWeight: 'bold',
                    },
                    cardStyle: {
                        backgroundColor: '#f5f5dc',
                    },
                }}
            >
                {/* 登录页面 */}
                <Stack.Screen
                    name="Login"
                    component={LoginScreen}
                    options={{
                        headerShown: false,
                    }}
                />
                
                {/* 注册页面 */}
                <Stack.Screen
                    name="Register"
                    component={RegisterScreen}
                    options={{
                        title: '注册账号',
                        headerBackTitle: '返回',
                    }}
                />
                
                {/* 小说列表页面 */}
                <Stack.Screen
                    name="NovelList"
                    component={NovelListScreen}
                    options={{
                        title: '我的书架',
                        headerLeft: () => null, // 隐藏默认返回按钮
                    }}
                />
                
                {/* 小说阅读器页面 */}
                <Stack.Screen
                    name="NovelReader"
                    component={NovelReaderScreen}
                    options={{
                        title: '阅读',
                        headerBackTitle: '返回',
                    }}
                />
                
                {/* 聊天页面 */}
                <Stack.Screen
                    name="Chat"
                    component={ChatScreen}
                    options={{
                        title: '聊天室',
                        headerBackTitle: '返回',
                    }}
                />
            </Stack.Navigator>
        </NavigationContainer>
    );
}

export default App;
