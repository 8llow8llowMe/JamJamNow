import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface UserSliceState {}

const initialState: UserSliceState = {}

export const UserSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setUser(state, action: PayloadAction<Partial<UserSliceState>>) {
      Object.assign(state, {
        ...state,
        ...action.payload,
      })
    },
    removeUser(state) {
      Object.assign(state, initialState)
    },
  },
})

export const { setUser, removeUser } = UserSlice.actions

export default UserSlice.reducer
