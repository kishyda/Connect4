import { useContext, type Dispatch } from "react"

export interface Session {
    userID: string | null | undefined,
    sessionID: string | null | undefined,
    username: string | null | undefined,
    email: string | null | undefined
}

export interface GlobalStore {
    session: Session | undefined,
    setSession: Dispatch<Session> | undefined,
}

