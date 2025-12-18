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

function App(): JSX.Element {
    return (
        <NavigationContainer>
            <Stack.Navigator initialRouteName="Login">
                <Stack.Screen name="Login" component={LoginScreen} />
                <Stack.Screen name="Register" component={RegisterScreen} />
                <Stack.Screen name="NovelList" component={NovelListScreen} options={{ title: 'Novels' }} />
                <Stack.Screen name="NovelReader" component={NovelReaderScreen} options={{ title: 'Reader' }} />
                <Stack.Screen name="Chat" component={ChatScreen} options={{ title: 'Chat Room' }} />
            </Stack.Navigator>
        </NavigationContainer>
    );
}

export default App;
