import Timer from "../components/Timer";

const Record = () => {
    // 영상 녹화 중엔 따로 표시해줄 화면은 없다.

    return ( 
        <div style ={{
            paddingTop : '800px',
        }}>
            <Timer></Timer>
        </div>
     );
}
 
export default Record;