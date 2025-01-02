import React from 'react';
import '../styles/MainVars.css';
import '../styles/MainStyle.css';

// 주가지수 데이터
const marketIndexData = [
    { name: "KOSPI", value: "2,550", change: "+1.28%", points: "32", image: "/group0.svg" },
    { name: "KOSDAQ", value: "870", change: "-0.52%", points: "-4.5", image: "/group1.svg" },
    { name: "NASDAQ", value: "13,678", change: "+2.10%", points: "280", image: "/group2.svg" },
    { name: "S&P 500", value: "4,380", change: "-0.78%", points: "-34", image: "/group3.svg" }
];

//인기 급상승 주식 데이터
const stockData = [
    { rank: 1, name: "삼성전자", volume: "12,228,100", price: "70,000", change: "+1.28%", points: "900" },
    { rank: 2, name: "SK하이닉스", volume: "9,456,200", price: "115,000", change: "-0.82%", points: "-950" },
    { rank: 3, name: "NAVER", volume: "4,230,000", price: "210,000", change: "+2.50%", points: "5,200" },
    { rank: 4, name: "LG에너지솔루션", volume: "5,000,000", price: "450,000", change: "+1.10%", points: "4,900" },
    { rank: 5, name: "카카오", volume: "6,400,000", price: "60,000", change: "-1.20%", points: "-700" },
    { rank: 6, name: "현대자동차", volume: "3,900,000", price: "180,000", change: "+0.80%", points: "1,400" },
    { rank: 7, name: "기아", volume: "2,850,000", price: "75,000", change: "+0.50%", points: "400" },
    { rank: 8, name: "POSCO홀딩스", volume: "3,500,000", price: "320,000", change: "+3.20%", points: "10,000" },
    { rank: 9, name: "LG화학", volume: "1,700,000", price: "580,000", change: "-0.50%", points: "-2,900" },
    { rank: 10, name: "KB금융", volume: "8,000,000", price: "55,000", change: "+1.00%", points: "550" }
];

const MainPage = () => {
    return (
        <div className="_0-1-home">
            <div className="frame-45">
                <div className="gnb">
                    <div className="frame-11">
                        <div className="frame-26">
                            <img className="image-6" src="/image-60.png" alt="Logo"/>
                            <div className="frame-10">
                                <div className="frame-9">
                                    <div className="gnb-button">
                                        <div className="div">홈으로</div>
                                    </div>
                                    <div className="gnb-button">
                                        <div className="div">로그인</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className="search-bar">
                            <div className="frame-1">
                                <img className="search-01" src="/search-010.svg" alt="Search Icon"/>
                                <div className="div2">검색하실 종목 이름을 입력해 주세요.</div>
                            </div>
                        </div>
                    </div>
                </div>
                <img className="image-9" src="/image-90.png" alt="Main Graphic"/>
                <div className="frame-44">
                    <div className="frame-43">
                        <div className="div3">
                            <div className="div4">📊 주가 지수</div>
                            {/* 주가지수 데이터 렌더링 */}
                            <div className="box">
                                {marketIndexData.map((index, i) => (
                                    <div className="dashboard" key={i}>
                                        <div className="frame-5">
                                            <div className="frame-4">
                                                <div className="kospi">{index.name}</div>
                                                <div className="frame-3">
                                                    <div className="_15-550">{index.value}</div>
                                                    <div className="div5">
                                                        <div
                                                            className="_1-28"
                                                            style={{
                                                                color: index.change.includes('-') ? '#2175F2' : '#FF4726'
                                                            }}
                                                        >
                                                            {index.change}
                                                        </div>
                                                        <div
                                                            className="_290"
                                                            style={{
                                                                color: index.points.includes('-') ? '#2175F2' : '#FF4726'
                                                            }}
                                                        >
                                                            {index.points}
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <img className="group" src={index.image} alt={`${index.name} Graphic`}/>
                                        </div>
                                    </div>
                                ))}

                            </div>
                        </div>
                        <div className="frame-24">
                            <div className="top-10">🔥 인기 급상승 종목 Top 10</div>
                            <div className="frame-14">
                                <div className="frame-23">
                                    <div className="frame-22">
                                        <div className="div6">순위</div>
                                        <div className="div7">종목</div>
                                        <div className="div8">거래량</div>
                                        <div className="div9">주가</div>
                                        <div className="div10">등락</div>
                                    </div>
                                </div>
                                {/* 데이터를 불러와 반복 랜더링 */}
                                {stockData.map((stock) => (
                                    <div className="rank-lable" key={stock.rank}>
                                        <div className="_3">{stock.rank}</div>
                                        <div className="div11">{stock.name}</div>
                                        <div className="_1-228-100">{stock.volume}</div>
                                        <div className="_1-228-1002">{stock.price}</div>
                                        <div className="div12">
                                            <div
                                                className="_1-282"
                                                style={{
                                                    color: stock.change.includes('-') ? '#2175F2' : '#FF4726'
                                                }}
                                            >
                                                {stock.change}
                                            </div>
                                            <div
                                                className="_2902"
                                                style={{
                                                    color: stock.points.includes('-') ? '#2175F2' : '#FF4726'
                                                }}
                                            >
                                                {stock.points}
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MainPage;