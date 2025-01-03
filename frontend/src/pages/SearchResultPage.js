import { React, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import '../styles/MainVars.css';
import '../styles/MainStyle.css';

// 검색한거에 맞게
const stockData = [
  {
    rank: 1,
    name: '삼성전자',
    volume: '12,228,100',
    price: '70,000',
    change: '+1.28%',
    points: '900',
  },
  {
    rank: 2,
    name: 'SK하이닉스',
    volume: '9,456,200',
    price: '115,000',
    change: '-0.82%',
    points: '-950',
  },
  {
    rank: 3,
    name: 'NAVER',
    volume: '4,230,000',
    price: '210,000',
    change: '+2.50%',
    points: '5,200',
  },
  {
    rank: 4,
    name: 'LG에너지솔루션',
    volume: '5,000,000',
    price: '450,000',
    change: '+1.10%',
    points: '4,900',
  },
  {
    rank: 5,
    name: '카카오',
    volume: '6,400,000',
    price: '60,000',
    change: '-1.20%',
    points: '-700',
  },
  {
    rank: 6,
    name: '현대자동차',
    volume: '3,900,000',
    price: '180,000',
    change: '+0.80%',
    points: '1,400',
  },
  {
    rank: 7,
    name: '기아',
    volume: '2,850,000',
    price: '75,000',
    change: '+0.50%',
    points: '400',
  },
  {
    rank: 8,
    name: 'POSCO홀딩스',
    volume: '3,500,000',
    price: '320,000',
    change: '+3.20%',
    points: '10,000',
  },
  {
    rank: 9,
    name: 'LG화학',
    volume: '1,700,000',
    price: '580,000',
    change: '-0.50%',
    points: '-2,900',
  },
  {
    rank: 10,
    name: 'KB금융',
    volume: '8,000,000',
    price: '55,000',
    change: '+1.00%',
    points: '550',
  },
];

const SearchResultPage = () => {
  const location = useLocation();
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();
  const query = new URLSearchParams(location.search).get('query');

  const filteredStocks = stockData.filter((stock) =>
    stock.name.includes(query)
  );

  const handleSearch = () => {
    if (searchTerm.trim()) {
      navigate(`/search?query=${searchTerm}`);
    }
  };

  return (
    <div className="_0-1-home">
      <div className="frame-45">
        {/* Header */}
        <div className="gnb">
          <div className="frame-11">
            <div className="frame-26">
              <img className="image-6" src="/image-60.png" alt="Logo" />
              <div className="frame-10">
                <div className="frame-9">
                  <div
                    className="gnb-button"
                    onClick={() => navigate('/')}
                    style={{ cursor: 'pointer' }}
                  >
                    홈으로
                  </div>
                  <div
                    className="gnb-button"
                    onClick={() => navigate('/login')}
                    style={{ cursor: 'pointer' }}
                  >
                    로그인
                  </div>
                </div>
              </div>
            </div>
            <div className="search-bar">
              <div className="frame-1">
                <img
                  className="search-01"
                  src="/search-010.svg"
                  alt="Search Icon"
                  style={{ cursor: 'pointer' }}
                  onClick={handleSearch}
                />
                <input
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  placeholder="검색하실 종목 이름을 입력해 주세요."
                  className="div2"
                />
              </div>
            </div>
          </div>
        </div>

        <img className="image-9" src="/image-90.png" alt="Main Graphic" />

        <div className="main-content">
          {/* Stock Ranking Section */}
          <div className="stock-ranking">
            <div className="top-10">
              🔎 다음에 대한 검색 결과 표시 중: {query}
            </div>
            <table className="stock-table">
              <thead>
                <tr>
                  <th>순위</th>
                  <th>종목</th>
                  <th>거래량</th>
                  <th>주가</th>
                  <th>등락</th>
                </tr>
              </thead>
              <tbody>
                {filteredStocks.map((stock) => (
                  <tr
                    key={stock.rank}
                    onClick={() => navigate(`/stock/${stock.name}`)} // 클릭 시 라우팅
                    style={{ cursor: 'pointer' }} // 클릭 가능한 스타일 추가
                  >
                    <td>{stock.rank}</td>
                    <td>{stock.name}</td>
                    <td>{stock.volume}</td>
                    <td>{stock.price}</td>
                    <td
                      style={{
                        color: stock.change.includes('-')
                          ? '#2175F2'
                          : '#FF4726',
                      }}
                    >
                      {stock.change} <span>{stock.points}</span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SearchResultPage;
