import { combineReducers, configureStore } from '@reduxjs/toolkit'
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux'
import {
  FLUSH,
  PAUSE,
  PERSIST,
  persistReducer,
  persistStore,
  PURGE,
  REGISTER,
  REHYDRATE,
} from 'redux-persist'
import storage from 'redux-persist/lib/storage'

import navButtonReducer from './features/navButton/navButtonSlice'
import userReducer from './features/user/userSlice'

const rootReducer = combineReducers({
  navButtonReducer,
  userReducer,
})

const persistConfig = {
  key: 'root',
  storage,
  whitelist: ['navButtonReducer', 'userReducer'],
}

const persistedReducer = persistReducer(persistConfig, rootReducer)

export const store = configureStore({
  reducer: persistedReducer,
  // devTools: isDev,
  middleware: getDefaultMiddleware =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
    }),
})

const persistor = persistStore(store)

type RootState = ReturnType<typeof store.getState>
type AppDispatch = typeof store.dispatch

const useAppDispatch = () => useDispatch<AppDispatch>()
const useAppSelector: TypedUseSelectorHook<RootState> = useSelector

export type { RootState, AppDispatch }
export { useAppDispatch, useAppSelector, persistor }
