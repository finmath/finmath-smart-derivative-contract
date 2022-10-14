import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
contract LiquidityToken is ERC20 {
    constructor(string memory name, string memory symbol) ERC20(name, symbol) {}

    function mint(address to, uint256 amount) public {
        _mint(to, amount);
    }
}
