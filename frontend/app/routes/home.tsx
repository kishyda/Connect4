import Header from "~/components/Header/Header";
import type { Route } from "./+types/home";
import Footer from "~/components/Footer/Footer";
import Board from "~/components/Board/Board";
import Buttons from "~/components/Buttons/Buttons";

export function meta({}: Route.MetaArgs) {
    return [
        { title: "Connect 4" },
        { name: "description", content: "" },
    ];
}

export default function Home() {

    const yCoordinates = [20, 60, 100, 140, 180, 220];
    const board = (Array.from({ length: 7 }, () => Array.from({ length: 6 }, () => ' ')));

    return (
        <div className="w-screen h-screen flex flex-col justify-between">
            <Header styles="flex items-center justify-center"/>
            <div className="flex flex-col items-center justify-center">
                <Buttons/>
                <Board styles="" handleCircleClick={() => {}} yCoordinates={yCoordinates} board={board} />
            </div>
            <Footer styles="flex items-center justify-center"/>
        </div>
    );
}
