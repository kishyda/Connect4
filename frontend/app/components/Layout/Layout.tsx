import Header from "../Header/Header";
import Footer from "../Footer/Footer"

export default function Layout( {children}: { children: React.ReactNode} ) {
    return (
        <div className="h-screen w-screen">
            <Header/>
            {children}
            <Footer/>
        </div>
    );
}
