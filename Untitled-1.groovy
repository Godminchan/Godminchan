<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>텍스트 RPG</title>
</head>

<body>
    <form id="start-screen">
        <input id="name-input" placeholder="주인공 이름을 입력하세요!" />
        <button id="start">시작</button>
    </form>
    <div id="screen">
        <div id="hero-stat">
            <span id="hero-name"></span>
            <span id="hero-level"></span>
            <span id="hero-hp"></span>
            <span id="hero-xp"></span>
            <span id="hero-att"></span>
        </div>
        <!-- 일반 메뉴 -->
        <form id="game-menu" style="display: none;">
            <div id="menu-1">1.모험</div>
            <div id="menu-2">2.휴식</div>
            <div id="menu-3">3.종료</div>
            <input id="menu-input" />
            <button id="menu-button">입력</button>
        </form>
        <!-- 전투 메뉴 -->
        <form id="battle-menu" style="display: none;">
            <div id="battle-1">1.공격</div>
            <div id="battle-2">2.회복</div>
            <div id="battle-3">3.도망</div>
            <input id="battle-input" />
            <button id="battle-button">입력</button>
        </form>
        <!-- 몬스터 스탯 -->
        <div id="message"></div>
        <div id="monster-stat">
            <span id="monster-name"></span>
            <span id="monster-hp"></span>
            <span id="monster-att"></span>
            <span id="monster-xp"></span>
        </div>
    </div>
    <script>
        const $startScreen = document.querySelector('#start-screen');
        const $gameMenu = document.querySelector('#game-menu');
        const $battleMenu = document.querySelector('#battle-menu');
        const $heroName = document.querySelector('#hero-name');
        const $heroLevel = document.querySelector('#hero-level');
        const $heroHp = document.querySelector('#hero-hp');
        const $heroXp = document.querySelector('#hero-xp');
        const $heroAtt = document.querySelector('#hero-att');
        const $monsterName = document.querySelector('#monster-name');
        const $monsterHp = document.querySelector('#monster-hp');
        const $monsterAtt = document.querySelector('#monster-att');
        const $monsterXp = document.querySelector('#monster-xp');
        const $message = document.querySelector('#message');

        class Game {
            constructor(name) {
                this.monster = null;
                this.hero = null;
                this.monsterList = [
                    { name: '슬라임', hp: 25, att: 10, xp: 10 },
                    { name: '스켈레톤', hp: 50, att: 15, xp: 20 },
                    { name: '마왕', hp: 150, att: 35, xp: 50 },
                ];
                this.start(name);
            }
            start(name) {
                //console.log(this);
                $gameMenu.addEventListener('submit', this.onGameMenuInput);
                $battleMenu.addEventListener('submit', this.onBattleMenuInput);
                this.changeScreen('game');//시작
                this.hero = new Hero(this, name); //히어로 생성
                this.updateHeroStat(); //스탯 업데이트
            }
            changeScreen(screen) {
                if (screen === 'start') {
                    $startScreen.style.display = 'block';
                    $gameMenu.style.display = 'none';
                    $battleMenu.style.display = 'none';
                } else if (screen === "game") {
                    $startScreen.style.display = 'none';
                    $gameMenu.style.display = 'block';
                    $battleMenu.style.display = 'none';
                } else if (screen === 'battle') {
                    $startScreen.style.display = 'none';
                    $gameMenu.style.display = 'none';
                    $battleMenu.style.display = 'block';
                }
            }
            onGameMenuInput = (event) => { // 화살표 함수를 썻기 때문에 밖 this를 받아옴.
                event.preventDefault();
                const input = event.target['menu-input'].value;
                if (input === '1') { //모험
                    //console.log(this);
                    this.changeScreen('battle');
                    const randomIndex = Math.floor(Math.random() * this.monsterList.length);
                    const randomMonster = this.monsterList[randomIndex];
                    this.monster = new Monster(
                        this,
                        randomMonster.name,
                        randomMonster.hp,
                        randomMonster.att,
                        randomMonster.xp,
                    );
                    this.updateMonsterStat();
                    //console.log(this.monster);
                    this.showMessage(`몬스터와 마추쳤다. ${this.monster.name} 인 거 같다!`);
                } else if (input === '2') {//휴식
                    console.log(this);
                    this.hero.hp = this.hero.maxHp;
                    this.updateHeroStat();
                    this.showMessage(`체력(: ${this.hero.hp})을 모두 회복 했습니다!.`)
                } else if (input === '3') {//종료
                    this.quit();
                    this.showMessage('게임을 종료합니다. 시작 화면으로 돌아왔습니다.');
                };
            }
            onBattleMenuInput = (event) => {
                event.preventDefault();
                const input = event.target['battle-input'].value;
                if (input === '1') { //공격
                    const { hero, monster } = this;
                    hero.attack(monster);
                    monster.attack(hero);

                    if (hero.hp <= 0) {//영웅이 뒤진 경우
                        this.showMessage(`${hero.lev} 레벨에서 전사. 새 주인공을 생성하세요.`);
                        this.quit();//게임 종료
                    } else if (monster.hp <= 0) {//영웅이 몬스터를 처죽인 경우
                        this.showMessage(`몬스터를 잡아 ${monster.xp} 경험치를 얻었다.`);
                        hero.getXp(monster.xp);
                        this.monster = null;
                        this.changeScreen('game');
                    } else {//서로 산 경우
                        this.showMessage(`${hero.att}의 데미지를 주고, ${monster.att}의 데미지를 받았다.`);
                    }

                    this.updateHeroStat();
                    this.updateMonsterStat();
                } else if (input === '2') {//
                    this.hero.heal(this.monster);
                    this.updateHeroStat();
                    console.log(this.hero);
                    this.showMessage(`체력을 20 회복 했지만, 몬스터에게 ${this.monster.att} 만큼 공격당했습니다!
                    \n 현재 체력 : ${this.hero.hp}`)
                } else if (input === '3') {//도망
                    this.changeScreen('game');
                    this.monster = null;
                    this.updateMonsterStat();
                    this.showMessage('도망갑니다! 메뉴 화면으로 돌아갑니다.');
                }

            }
            updateHeroStat() {
                const { hero } = this;
                if (hero === null) {
                    $heroName.textContent = '';
                    $heroLevel.textContent = '';
                    $heroHp.textContent = '';
                    $heroXp.textContent = '';
                    $heroAtt.textContent = '';
                    return;
                }
                //console.log(hero,this);
                $heroName.textContent = hero.name;
                $heroLevel.textContent = `${hero.lev}Lev`;
                $heroHp.textContent = `HP: ${hero.hp}/${hero.maxHp}`;
                $heroXp.textContent = `XP : ${hero.xp}/${15 * hero.lev}`;
                $heroAtt.textContent = `ATT : ${hero.att}`;
                //display(hero);
            }
            updateMonsterStat() {
                const { monster } = this;
                if (monster === null) {
                    $monsterName.textContent = '';
                    $monsterHp.textContent = '';
                    $monsterAtt.textContent = '';
                    $monsterXp.textContent = '';
                    return;
                }
                //console.log(monster);
                $monsterName.textContent = monster.name;
                $monsterHp.textContent = `HP: ${monster.hp}/${monster.maxHp}`;
                $monsterAtt.textContent = `ATT: ${monster.att}`;
                $monsterXp.textContent = `XP : ${monster.xp}`;
            }
            showMessage(text) {
                $message.textContent = text;
            }
            quit() {
                this.hero = null;
                this.monster = null;
                this.updateHeroStat();
                this.updateMonsterStat();
                $gameMenu.removeEventListener('submit', this.onGameMenuInput);
                $battleMenu.removeEventListener('submit', this.onBattleMenuInput);
                this.changeScreen('start');
                game = null;
            }
        }
        class Unit {
            constructor(game, name, hp, att, xp) {
                this.game = game;
                this.name = name;
                this.maxHp = hp;
                this.hp = hp;
                this.xp = xp;
                this.att = att;
            }
            attack(target) {
                target.hp -= this.att;
            }
        }
        class Hero extends Unit{
            constructor(game, name) {
                super(game,name,100,10,0);
                this.lev = 1;
            }
            heal(target) {
                console.log(this.maxHp);
                if(this.hp + 20 <= this.maxHp){
                    this.hp += 20;
                }
                else{
                    this.hp = this.maxHp;
                }
                this.hp -= target.att;
            }
            getXp(xp) {
                this.xp += xp;
                if (this.xp >= this.lev * 15) { 
                    this.xp -= this.lev * 15;
                    this.lev += 1;
                    this.maxHp += 5;
                    this.att += 5;
                    this.hp = this.maxHp;
                    this.game.showMessage(`레벨업! 레벨 ${this.lve}`);
                }
            }
        }

        class Monster extends Unit {
            constructor(game, name, hp, att, xp) {
                super(game,name,hp,att,xp);
            }

        }

        let game = null;
        $startScreen.addEventListener('submit', (event) => {
            event.preventDefault();
            const name = event.target['name-input'].value;
            game = new Game(name);
        });

        // const display = (hero) => (event) => {
        //     console.log(hero);
        //     $heroName.textContent = hero.name;
        //     $heroLevel.textContent = `${hero.lev}Lev`;
        //     $heroHp.textContent = `HP : ${hero.hp}/${hero.maxHp}`;
        //     $heroXp.textContent = `XP : ${hero.Xp}/${hero.lev}`;
        //     $heroAtt.textContent = `ATT : ${hero.att}`;
        //     // $message.textContent = text;
        // };

    </script>
</body>

</html>