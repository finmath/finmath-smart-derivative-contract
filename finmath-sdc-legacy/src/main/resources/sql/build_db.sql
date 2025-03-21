--
-- TOC entry 219 (class 1259 OID 16749)
-- Name: MarketDataPoints; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."MarketDataPoints" (
    "dataTimestamp_" timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "symbolId_" character varying(60) NOT NULL,
    value_ double precision NOT NULL,
    owner_ integer NOT NULL,
    source_ character varying(10) NOT NULL
);


ALTER TABLE public."MarketDataPoints" OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16748)
-- Name: MarketDataPoints_owner_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."MarketDataPoints_owner_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."MarketDataPoints_owner_seq" OWNER TO postgres;

--
-- TOC entry 3336 (class 0 OID 0)
-- Dependencies: 218
-- Name: MarketDataPoints_owner_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."MarketDataPoints_owner_seq" OWNED BY public."MarketDataPoints".owner_;


--
-- TOC entry 217 (class 1259 OID 16742)
-- Name: Users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Users" (
    userid integer NOT NULL,
    username character varying(60) NOT NULL
);


ALTER TABLE public."Users" OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16741)
-- Name: User_userid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."User_userid_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."User_userid_seq" OWNER TO postgres;

--
-- TOC entry 3337 (class 0 OID 0)
-- Dependencies: 216
-- Name: User_userid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."User_userid_seq" OWNED BY public."Users".userid;


--
-- TOC entry 3182 (class 2604 OID 16753)
-- Name: MarketDataPoints owner_; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."MarketDataPoints" ALTER COLUMN owner_ SET DEFAULT nextval('public."MarketDataPoints_owner_seq"'::regclass);


--
-- TOC entry 3180 (class 2604 OID 16745)
-- Name: Users userid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Users" ALTER COLUMN userid SET DEFAULT nextval('public."User_userid_seq"'::regclass);


--
-- TOC entry 3186 (class 2606 OID 16755)
-- Name: MarketDataPoints MarketDataPoints_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."MarketDataPoints"
    ADD CONSTRAINT "MarketDataPoints_pkey" PRIMARY KEY ("dataTimestamp_", "symbolId_");


--
-- TOC entry 3184 (class 2606 OID 16747)
-- Name: Users User_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Users"
    ADD CONSTRAINT "User_pkey" PRIMARY KEY (userid);


--
-- TOC entry 3187 (class 2606 OID 16756)
-- Name: MarketDataPoints FK_OWNERSHIP; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."MarketDataPoints"
    ADD CONSTRAINT "FK_OWNERSHIP" FOREIGN KEY (owner_) REFERENCES public."Users"(userid) ON UPDATE CASCADE ON DELETE CASCADE NOT VALID;


-- Completed on 2023-07-04 11:28:16

--
-- PostgreSQL database dump complete
--

