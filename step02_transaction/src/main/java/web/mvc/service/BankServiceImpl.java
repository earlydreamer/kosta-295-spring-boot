package web.mvc.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import web.mvc.domain.Bank;
import web.mvc.dto.RequestTransferDTO;
import web.mvc.exception.BasicException;
import web.mvc.exception.ErrorCode;
import web.mvc.repository.BankRepository;

@Service
@RequiredArgsConstructor

public class BankServiceImpl implements  BankService{

    private final BankRepository bankRepository; //Spring Data JPA가 구현객체 생성해서 주입해줌


    @Transactional(rollbackOn = Exception.class) //Exception이면 rollback해라)
    @Override
    public int transfer(RequestTransferDTO requestTransferDTO) throws BasicException {

    //출금계좌에서 금액만큼 인출
        Bank outBank = bankRepository.findById(requestTransferDTO.getOutAccount()).
                orElseThrow(()->new BasicException(ErrorCode.FAILED_WITHDRAWAL_ACCOUNT));

        outBank.setBalance(outBank.getBalance() - requestTransferDTO.getAmount());

        //입금계좌에 금액만큼 입금
        Bank inBank = bankRepository.findById(requestTransferDTO.getInAccount())
                .orElseThrow(()->new BasicException(ErrorCode.FAILED_DEPOSIT_ACCOUNT));

        inBank.setBalance(inBank.getBalance()+ requestTransferDTO.getAmount());


        //잔액확인
        if( inBank.getBalance() > 1000)
            throw new  BasicException(ErrorCode.FAILED_MAXIMUM);


        return 1;
    }
}
