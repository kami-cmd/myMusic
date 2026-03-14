package com.gwc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gwc.entity.Result;
import com.gwc.service.EmailService;
import com.gwc.vo.KamiVO;
import com.gwc.entity.Kami;
import com.gwc.entity.PageResult;
import com.gwc.mapper.KamiMapper;
import com.gwc.service.IKamiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.gwc.utils.StringContent.KAMI_VERIFICATION;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 购物车
 * @since 2026-02-12
 */
@Service
public class KamiServiceImpl extends ServiceImpl<KamiMapper, Kami> implements IKamiService {

    @Autowired
    private EmailService emailService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResult pageQuery(KamiVO kamiVO) {
        //1.创造分页插件
        Page page = new Page(kamiVO.getCurrentPage(), kamiVO.getPageSize());

        //2.设置查询条件
        //2.1是否要进行排序
        boolean sort = StringUtils.isNotBlank(kamiVO.getSortField()) && StringUtils.isNotBlank(kamiVO.getSortOrder());
        if (sort) {
            //2.2确定要进行排序,对排序的字段进行处理
            String sortField = kamiVO.getSortField();
            for (int i = 0; i < sortField.length(); i++) {
                char now = sortField.charAt(i);
                if (now >= 65 && now <= 90) {
                    //2.3是大写字母
                    now += 32;
                    kamiVO.setSortField(sortField.substring(0, i) + "_" + now + sortField.substring(i + 1));
                    break;
                }
            }
        }
        LambdaQueryWrapper<Kami> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(kamiVO.getKamiName()), Kami::getKamiName, kamiVO.getKamiName())
                .like(StringUtils.isNotBlank(kamiVO.getPhone()), Kami::getPhone, kamiVO.getPhone())
                .like(StringUtils.isNotBlank(kamiVO.getEmail()), Kami::getEmail, kamiVO.getEmail())
                .last(sort, "order by " + kamiVO.getSortField() + " " + kamiVO.getSortOrder());

        wrapper.ge(kamiVO.getStartDate() != null, Kami::getCreateTime, kamiVO.getStartDate())
                .le(kamiVO.getEndDate() != null, Kami::getCreateTime, kamiVO.getEndDate());

        //3.分页查询
        Page result = page(page, wrapper);
        //4.转为kami
        List<Kami> kamiList = result.getRecords();
        if (kamiList == null || kamiList.isEmpty()) {
            //5.如果是空就把直接不用管
            return new PageResult(result.getTotal(), Collections.emptyList());
        }
        //6.再把kami里面的手机号和邮箱进行星号处理
        for (Kami kami : kamiList) {
            rewritePhAndEm(kami);
        }
        //7.返回结果
        return new PageResult(result.getTotal(), kamiList);
    }

    @Override
    public void exportList(HttpServletResponse response) {
        //拿到所有数据
        List<Kami> kamiLst = lambdaQuery().list();
        if (kamiLst.isEmpty()) {
            //如果没有数据
            throw new RuntimeException("没有管理员数据");
        }
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("templates/KamiList.xlsx");
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheet("Sheet1");
            int begin = 2;
            for (Kami kami : kamiLst) {
                XSSFRow row = sheet.createRow(begin);
                row.createCell(0).setCellValue(kami.getKamiName());
                row.createCell(2).setCellValue(kami.getPhone());
                row.createCell(4).setCellValue(kami.getEmail());
                begin++;
            }
            //写完后写出
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
            //关流
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Kami searchById(Long id) {
        //1.查到kami
        Kami kami = lambdaQuery().eq(Kami::getId, id).one();
        if (kami == null) {
            //1.1如果不存在,直接返回
            return null;
        }
        //2.将手机号和邮箱分别进行打码
        rewritePhAndEm(kami);
        //3.返回结果
        return kami;
    }

    @Override
    public void sendVerification(String email) {
        //1.通过hutool创建验证码(6位数字)
        String verification = RandomUtil.randomNumbers(6);
        //2.发送验证码给用户
        emailService.sendCode(email,verification);
        //3.将验证码保存到redis中
        stringRedisTemplate.opsForValue().set(KAMI_VERIFICATION+email,verification,5, TimeUnit.MINUTES);
    }

    @Override
    public Result updateByKami(KamiVO kamiVO) {
        //1.判断邮箱修改吗
        if(StrUtil.isNotBlank(kamiVO.getEmail())){
            //1.1修改
            //1.2验证码为空,返回
            if(!StrUtil.isNotBlank(kamiVO.getEmail())){
                return Result.error("验证码不能为空");
            }
            String verification = stringRedisTemplate.opsForValue().get(KAMI_VERIFICATION+kamiVO.getEmail());
            //1.3验证码不存在,返回
            if(verification==null||verification.isEmpty()){
                return Result.error("验证码无效");
            }
            //1.4验证码错误,返回
            if(!verification.equals(kamiVO.getVerificationCode())){
                return Result.error("验证码有误");
            }
            //1.5都有效后将验证码删除
            stringRedisTemplate.delete(KAMI_VERIFICATION+kamiVO.getEmail());
        }
        //2.直接进行修改
        updateById(BeanUtil.copyProperties(kamiVO,Kami.class));
        //3.结束
        return Result.success();
    }

    private static void rewritePhAndEm(Kami kami) {
        //1.1先拿到手机号和邮箱
        String phone = kami.getPhone();
        String email = kami.getEmail();
        //1.2对手机号的中间4位进行打码
        phone = StrUtil.hide(phone, 3, 7);
        //1.3对邮箱的数字进行打码
        //1.3.1拿到前面的纯数字以及后缀
        String emailSuffix = email.substring(email.indexOf("@"));
        email = email.substring(0, email.indexOf("@"));
        String hidedEmail;
        if (email.length() > 4) {
            //1.3.2如果数字过4就对后4位进行打码
            int beginIndex = email.length() - 4;
            hidedEmail = StrUtil.hide(email, beginIndex, email.length());
        } else {
            //1.3.3如果数字没有过4就全部打码
            hidedEmail = StrUtil.hide(email, 0, email.length());
        }
        //1.4对邮箱进行拼接
        email = hidedEmail + emailSuffix;
        //1.5把邮箱和手机号放进去
        kami.setPhone(phone).setEmail(email);
    }

}
